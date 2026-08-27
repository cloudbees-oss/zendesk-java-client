package org.zendesk.client.v2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.After;
import org.junit.Test;

public class SharedFutureTokenProviderTest {

  private static final Instant T0 = Instant.parse("2026-01-01T00:00:00Z");
  private static final double REFRESH_THRESHOLD = 0.5;
  private static final Duration LIFETIME = Duration.ofMinutes(30);
  private static final Duration INTO_REFRESH_WINDOW = Duration.ofMinutes(20);
  private static final Duration PAST_EXPIRY = LIFETIME.plusMinutes(1);

  /** Mirrors {@code SharedFutureTokenProvider.REFRESH_BACKOFF_DURATION}. */
  private static final Duration REFRESH_BACKOFF_DURATION = Duration.ofSeconds(10);

  private final MutableClock clock = new MutableClock(T0);
  private final List<ExecutorService> pools = new ArrayList<>();

  @After
  public void shutdownPools() {
    pools.forEach(ExecutorService::shutdownNow);
  }

  //////////////////////////////////////////////////////////////////////
  // Refresh policy
  //////////////////////////////////////////////////////////////////////

  @Test
  public void serveFreshTokenWithoutMinting() {
    var minter = new FakeMinter();
    var provider = provider(minter);

    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");
    clock.advance(Duration.ofMinutes(5));
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");

    assertThat(minter.mintCount).hasValue(1);
  }

  @Test
  public void reMintOnceInRefreshWindow() {
    var minter = new FakeMinter();
    var provider = provider(minter);
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");

    clock.advance(INTO_REFRESH_WINDOW);

    assertThat(provider.provideBearerToken()).isEqualTo("tok-2");
    assertThat(provider.provideBearerToken()).isEqualTo("tok-2");
    assertThat(minter.mintCount).hasValue(2);
  }

  @Test
  public void reMintOnlyPastRefreshWindow() {
    // With a 0.5 threshold on a 30-minute lifetime, the boundary is 15 minutes remaining.
    long[][] cases = {{14, 1}, {15, 2}, {16, 2}};

    for (long[] testCase : cases) {
      var elapsedMinutes = testCase[0];
      var expectedMints = (int) testCase[1];
      var caseClock = new MutableClock(T0);
      var minter = new FakeMinter(caseClock);
      var provider = new SharedFutureTokenProvider(minter, caseClock, REFRESH_THRESHOLD);
      provider.provideBearerToken();

      caseClock.advance(Duration.ofMinutes(elapsedMinutes));
      provider.provideBearerToken();

      assertThat(minter.mintCount)
          .as("mints after %d minutes elapsed", elapsedMinutes)
          .hasValue(expectedMints);
    }
  }

  @Test
  public void servableTokenBoundary() {
    var minter = new FakeMinter();
    var provider = provider(minter);
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");

    minter.failWith(new ZendeskOAuthException("mint failed"));

    // Right before expiry: cached token is still servable on mint failure.
    clock.advance(LIFETIME.minusNanos(1));
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");

    // At expiry: no longer servable, so the mint failure surfaces.
    clock.advance(Duration.ofNanos(1));
    assertThatThrownBy(provider::provideBearerToken).isInstanceOf(ZendeskOAuthException.class);
  }

  @Test
  public void invalidRefreshThresholdExpectException() {
    double[] invalid = {
      0.0, 1.0, -0.1, 1.5, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
    };

    for (double threshold : invalid) {
      assertThatThrownBy(() -> new SharedFutureTokenProvider(new FakeMinter(), clock, threshold))
          .as("threshold %s", threshold)
          .isInstanceOf(IllegalArgumentException.class);
    }
  }

  //////////////////////////////////////////////////////////////////////
  // Single-flight and blocking
  //////////////////////////////////////////////////////////////////////

  @Test
  public void coldStartMintsOnceAcrossConcurrentThreads() {
    var gate = new CountDownLatch(1);
    var minter = new FakeMinter();
    minter.freezeMintUntil(gate);
    var provider = provider(minter);

    var calls = startRacingThreads(8, provider);
    await().atMost(5, TimeUnit.SECONDS).until(() -> minter.threadsInsideMint.get() == 1);
    gate.countDown();
    var results = awaitResults(calls);

    assertThat(minter.mintCount).hasValue(1);
    assertThat(minter.peakConcurrentMints).hasValueLessThanOrEqualTo(1);
    assertThat(results).allMatch(Result::succeeded).extracting(Result::value).containsOnly("tok-1");
  }

  @Test
  public void expiredTokenBlocksOnSharedMint() throws Exception {
    var minter = new FakeMinter();
    var provider = provider(minter);
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");

    clock.advance(PAST_EXPIRY);
    var gate = new CountDownLatch(1);
    minter.freezeMintUntil(gate);

    var pool = pool(2);
    var leader = startMintingLeader(pool, provider, minter);

    // The cached token is unusable, so neither thread may be handed anything until the mint lands.
    var follower = startParkedFollower(pool, provider);
    assertThat(leader.isDone()).isFalse();
    assertThat(follower.isDone()).isFalse();

    gate.countDown();
    assertThat(leader.get(5, TimeUnit.SECONDS).value()).isEqualTo("tok-2");
    assertThat(follower.get(5, TimeUnit.SECONDS).value()).isEqualTo("tok-2");
    assertThat(minter.mintCount).hasValue(2);
    assertThat(minter.peakConcurrentMints).hasValueLessThanOrEqualTo(1);
  }

  @Test
  public void followersInRefreshWindowDoNotBlock() throws Exception {
    var minter = new FakeMinter();
    var provider = provider(minter);
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");

    clock.advance(INTO_REFRESH_WINDOW);
    var gate = new CountDownLatch(1);
    minter.freezeMintUntil(gate);

    var pool = pool(2);
    var leader = startMintingLeader(pool, provider, minter);
    var follower = pool.submit(() -> call(provider));

    // The cached token is stale but still valid, so the follower keeps using it.
    assertThat(follower.get(2, TimeUnit.SECONDS).value()).isEqualTo("tok-1");
    assertThat(leader.isDone()).isFalse();

    gate.countDown();
    assertThat(leader.get(5, TimeUnit.SECONDS).value()).isEqualTo("tok-2");
    assertThat(minter.mintCount).hasValue(2);
    assertThat(minter.peakConcurrentMints).hasValueLessThanOrEqualTo(1);
  }

  @Test
  public void leaderBlocksWhileRefreshingInWindow() throws Exception {
    var minter = new FakeMinter();
    var provider = provider(minter);
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");

    clock.advance(INTO_REFRESH_WINDOW);
    var gate = new CountDownLatch(1);
    minter.freezeMintUntil(gate);

    var pool = pool(1);
    var leader = startMintingLeader(pool, provider, minter);

    // Inside the mint with the gate still closed, so the leader cannot have returned: it is
    // minting synchronously even though its cached token is still usable.
    assertThat(leader.isDone()).isFalse();

    gate.countDown();
    assertThat(leader.get(5, TimeUnit.SECONDS).value()).isEqualTo("tok-2");
  }

  //////////////////////////////////////////////////////////////////////
  // Failure handling
  //////////////////////////////////////////////////////////////////////

  @Test
  public void failedMintExpectSameExceptionForAllAwaiters() throws Exception {
    var gate = new CountDownLatch(1);
    var minter = new FakeMinter();
    minter.freezeMintUntil(gate);
    minter.failWith(new ZendeskOAuthException("mint failed"));
    var provider = provider(minter);

    var pool = pool(2);
    var leader = startMintingLeader(pool, provider, minter);
    var follower = startParkedFollower(pool, provider);

    gate.countDown();
    var leaderResult = leader.get(5, TimeUnit.SECONDS);
    var followerResult = follower.get(5, TimeUnit.SECONDS);

    assertThat(leaderResult.failed()).isTrue();
    assertThat(followerResult.failed()).isTrue();

    // No thread in the round attempts to re-mint.
    assertThat(followerResult.error()).isSameAs(leaderResult.error());
    assertThat(minter.mintCount).hasValue(1);
  }

  @Test
  public void failedMintServesStillValidToken() {
    var minter = new FakeMinter();
    var provider = provider(minter);
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");

    minter.failWith(new ZendeskOAuthException("mint failed"));

    clock.advance(INTO_REFRESH_WINDOW);
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");
  }

  @Test
  public void failedMintExpectExceptionWhenTokenExpiredDuringMint() {
    var minter = new FakeMinter();
    var provider = provider(minter);
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");

    // The cached token is no longer servable by the end of the mint process.
    minter.advanceClockDuringMint(Duration.ofMinutes(11));
    minter.failWith(new ZendeskOAuthException("mint failed"));

    clock.advance(INTO_REFRESH_WINDOW);
    assertThatThrownBy(provider::provideBearerToken).isInstanceOf(ZendeskOAuthException.class);
  }

  @Test
  public void failedMintClearsInFlightForNextWave() {
    var minter = new FakeMinter();
    var provider = provider(minter);

    minter.failWith(new ZendeskOAuthException("mint failed"));
    assertThatThrownBy(provider::provideBearerToken).isInstanceOf(ZendeskOAuthException.class);

    minter.stopFailing();
    assertThat(provider.provideBearerToken()).isEqualTo("tok-2");
    assertThat(minter.mintCount).hasValue(2);
  }

  @Test
  public void mintErrorFailsAwaitingThreadsInsteadOfHangingThem() throws Exception {
    var gate = new CountDownLatch(1);
    var minter = new FakeMinter();
    var provider = provider(minter);

    // An Error is outside the catch, so only the finally block can release a parked thread.
    minter.failWith(new Error("boom"));
    minter.freezeMintUntil(gate);

    var pool = pool(2);
    var leader = startMintingLeader(pool, provider, minter);
    var follower = startParkedFollower(pool, provider);

    gate.countDown();

    // This get() is the assertion: without the finally guard nothing completes the round, so the
    // follower stays parked and this times out.
    var followerResult = follower.get(5, TimeUnit.SECONDS);
    assertThat(followerResult.error())
        .isInstanceOf(ZendeskOAuthException.class)
        .hasMessageContaining("did not complete");

    assertThat(leader.get(5, TimeUnit.SECONDS).error()).isInstanceOf(Error.class);
  }

  @Test
  public void mintErrorClearsInFlightForNextWave() {
    var minter = new FakeMinter();
    var provider = provider(minter);

    minter.failWith(new Error("boom"));
    assertThatThrownBy(provider::provideBearerToken).isInstanceOf(Error.class);

    minter.stopFailing();
    assertThat(provider.provideBearerToken()).isEqualTo("tok-2");
    assertThat(minter.mintCount).hasValue(2);
  }

  @Test
  public void interruptedAwaitRestoresInterruptFlag() throws Exception {
    var gate = new CountDownLatch(1);
    var minter = new FakeMinter();
    minter.freezeMintUntil(gate);
    var provider = provider(minter);
    var followerThread = new AtomicReference<Thread>();
    var wasInterrupted = new AtomicBoolean();

    var pool = pool(2);
    startMintingLeader(pool, provider, minter);

    var follower =
        pool.submit(
            () -> {
              followerThread.set(Thread.currentThread());
              try {
                return call(provider);
              } finally {
                wasInterrupted.set(Thread.currentThread().isInterrupted());
              }
            });
    awaitParkedOnSharedResult(followerThread);

    followerThread.get().interrupt();
    var result = follower.get(5, TimeUnit.SECONDS);

    assertThat(result.failed()).isTrue();
    assertThat(result.error()).isInstanceOf(ZendeskOAuthException.class);
    assertThat(wasInterrupted).isTrue();
    gate.countDown();
  }

  @Test
  public void awaiterFallsBackToServableTokenWhenLeaderMintFails() throws Exception {
    var minter = new FakeMinter();
    var provider = provider(minter);

    // Reproduces a caller that captured an expired token, then waited on a failed refresh after
    // another thread had already published a servable token.
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");
    clock.advance(PAST_EXPIRY);

    var pool = pool(2);
    var awaiter =
        pool.submit(
            () -> {
              clock.stopThreadAtNextFreshnessCheck(Thread.currentThread());
              return call(provider);
            });
    clock.awaitThreadStoppedAfterTokenSnapshot();

    // Publish a fresh token while the awaiter still holds its expired snapshot.
    assertThat(provider.provideBearerToken()).isEqualTo("tok-2");

    // Start a failed refresh round that the awaiter will join.
    clock.advance(INTO_REFRESH_WINDOW);
    var gate = new CountDownLatch(1);
    minter.freezeMintUntil(gate);
    minter.failWith(new ZendeskOAuthException("mint failed"));
    var leader = startMintingLeader(pool, provider, minter);

    // Release the awaiter after it rejects its expired snapshot and joins the in-flight round.
    clock.resumeStoppedThread();
    clock.awaitExpiredSnapshotRejected();

    gate.countDown();

    var leaderResult = leader.get(5, TimeUnit.SECONDS);
    var awaiterResult = awaiter.get(5, TimeUnit.SECONDS);

    assertThat(leaderResult.value())
        .as("leader serves the servable cached token")
        .isEqualTo("tok-2");
    assertThat(awaiterResult.succeeded())
        .as("awaiter must fall back to the servable cached token, not inherit the failed mint")
        .isTrue();
    assertThat(awaiterResult.value()).isEqualTo("tok-2");
  }

  //////////////////////////////////////////////////////////////////////
  // Backoff after failed mints
  //////////////////////////////////////////////////////////////////////

  @Test
  public void failedMintSchedulesBackoffSuppressingNextMintWhileServable() {
    var minter = new FakeMinter();
    var provider = provider(minter);
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");

    minter.failWith(new ZendeskOAuthException("mint failed"));
    clock.advance(INTO_REFRESH_WINDOW);

    // The leader fails, serves the still-servable token, and schedules backoff.
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");
    assertThat(minter.mintCount).hasValue(2);

    // Inside the backoff with the token still servable
    clock.advance(REFRESH_BACKOFF_DURATION.dividedBy(2));
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");
    assertThat(minter.mintCount).hasValue(2);
  }

  @Test
  public void backoffElapsesThenReMints() {
    var minter = new FakeMinter();
    var provider = provider(minter);
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");

    minter.failWith(new ZendeskOAuthException("mint failed"));
    clock.advance(INTO_REFRESH_WINDOW);
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");
    assertThat(minter.mintCount).hasValue(2);

    minter.stopFailing();
    clock.advance(REFRESH_BACKOFF_DURATION.plusSeconds(1));
    assertThat(provider.provideBearerToken()).isEqualTo("tok-3");
    assertThat(minter.mintCount).hasValue(3);
  }

  @Test
  public void coldStartMintFailureKeepsRetryingWithoutBackoff() {
    var minter = new FakeMinter();
    var provider = provider(minter);
    minter.failWith(new ZendeskOAuthException("mint failed"));

    assertThatThrownBy(provider::provideBearerToken).isInstanceOf(ZendeskOAuthException.class);
    assertThat(minter.mintCount).hasValue(1);

    assertThatThrownBy(provider::provideBearerToken).isInstanceOf(ZendeskOAuthException.class);
    assertThat(minter.mintCount).hasValue(2);
  }

  @Test
  public void backoffStopsSuppressingOnceServableTokenExpires() {
    var minter = new FakeMinter();
    var provider = provider(minter);
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");

    clock.advance(LIFETIME.minusNanos(1));
    minter.failWith(new ZendeskOAuthException("mint failed"));
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");
    assertThat(minter.mintCount).hasValue(2);

    // Still servable and inside backoff at the same instant: suppressed.
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");
    assertThat(minter.mintCount).hasValue(2);

    // Once the cached token expires, backoff no longer suppresses minting.
    minter.stopFailing();
    clock.advance(Duration.ofNanos(1));
    assertThat(provider.provideBearerToken()).isEqualTo("tok-3");
    assertThat(minter.mintCount).hasValue(3);
  }

  @Test
  public void backoffBoundaryAttemptsMintAtBackoffExpiry() {
    var minter = new FakeMinter();
    var provider = provider(minter);
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");

    minter.failWith(new ZendeskOAuthException("mint failed"));
    clock.advance(INTO_REFRESH_WINDOW);
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");
    assertThat(minter.mintCount).hasValue(2);

    clock.advance(REFRESH_BACKOFF_DURATION.minusNanos(1));
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");
    assertThat(minter.mintCount).hasValue(2);

    clock.advance(Duration.ofNanos(1));
    assertThat(provider.provideBearerToken()).isEqualTo("tok-1");
    assertThat(minter.mintCount).hasValue(3);
  }

  //////////////////////////////////////////////////////////////////////
  // Test helpers
  //////////////////////////////////////////////////////////////////////

  /** Submits a thread that wins the election, returning once it is frozen inside the mint. */
  private Future<Result> startMintingLeader(
      ExecutorService pool, TokenProvider provider, FakeMinter minter) {
    var leader = pool.submit(() -> call(provider));
    await().atMost(5, TimeUnit.SECONDS).until(() -> minter.threadsInsideMint.get() == 1);
    return leader;
  }

  /**
   * Submits a thread that loses the election, returning once it is parked on the leader's round.
   */
  private Future<Result> startParkedFollower(ExecutorService pool, TokenProvider provider) {
    var followerThread = new AtomicReference<Thread>();
    var follower =
        pool.submit(
            () -> {
              followerThread.set(Thread.currentThread());
              return call(provider);
            });
    awaitParkedOnSharedResult(followerThread);
    return follower;
  }

  /**
   * Waits until the given thread has actually parked awaiting the shared mint result, so a test can
   * act on a genuine follower rather than sleeping and hoping.
   */
  private void awaitParkedOnSharedResult(AtomicReference<Thread> threadRef) {
    await()
        .atMost(5, TimeUnit.SECONDS)
        .until(
            () -> {
              var thread = threadRef.get();
              return thread != null && thread.getState() == Thread.State.WAITING;
            });
  }

  private SharedFutureTokenProvider provider(FakeMinter minter) {
    return new SharedFutureTokenProvider(minter, clock, REFRESH_THRESHOLD);
  }

  private ExecutorService pool(int threads) {
    var pool = Executors.newFixedThreadPool(threads);
    pools.add(pool);
    return pool;
  }

  /**
   * Submits threads held at a barrier, so they are released together and contend as hard as
   * possible for the single-flight probe.
   */
  private List<Future<Result>> startRacingThreads(int threads, TokenProvider provider) {
    var pool = pool(threads);
    var startLine = new CyclicBarrier(threads);
    var futures = new ArrayList<Future<Result>>(threads);
    for (var i = 0; i < threads; i++) {
      futures.add(
          pool.submit(
              () -> {
                startLine.await();
                return call(provider);
              }));
    }
    return futures;
  }

  private static List<Result> awaitResults(List<Future<Result>> futures) {
    var results = new ArrayList<Result>(futures.size());
    for (var future : futures) {
      try {
        results.add(future.get(10, TimeUnit.SECONDS));
      } catch (Exception e) {
        throw new AssertionError("thread did not finish", e);
      }
    }
    return results;
  }

  private static Result call(TokenProvider provider) {
    try {
      return new Result(provider.provideBearerToken(), null);
    } catch (Throwable t) {
      return new Result(null, t);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T extends Throwable> void sneakyThrow(Throwable t) throws T {
    throw (T) t;
  }

  private static void awaitLatch(CountDownLatch latch) {
    try {
      latch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private static final class Result {
    private final String value;
    private final Throwable error;

    Result(String value, Throwable error) {
      this.value = value;
      this.error = error;
    }

    String value() {
      return value;
    }

    Throwable error() {
      return error;
    }

    boolean succeeded() {
      return value != null && error == null;
    }

    boolean failed() {
      return value == null && error != null;
    }
  }

  /**
   * A fully controllable mint. {@code peakConcurrentMints} records the peak number of threads
   * inside {@link #mint()} at once.
   */
  private final class FakeMinter implements TokenMinter {
    private final AtomicInteger mintCount = new AtomicInteger();
    private final AtomicInteger threadsInsideMint = new AtomicInteger();
    private final AtomicInteger peakConcurrentMints = new AtomicInteger();
    private final AtomicReference<MintBehavior> behavior =
        new AtomicReference<>(new MintBehavior(() -> {}, null));
    private final Clock mintClock;

    FakeMinter() {
      this(SharedFutureTokenProviderTest.this.clock);
    }

    FakeMinter(Clock mintClock) {
      this.mintClock = mintClock;
    }

    /** Parks every mint inside {@link #mint()} until the latch opens. */
    void freezeMintUntil(CountDownLatch gate) {
      behavior.updateAndGet(current -> new MintBehavior(() -> awaitLatch(gate), current.failure));
    }

    /** Moves the clock on while a mint is in progress, to age a token mid-refresh. */
    void advanceClockDuringMint(Duration delta) {
      behavior.updateAndGet(
          current -> new MintBehavior(() -> clock.advance(delta), current.failure));
    }

    void failWith(Throwable failure) {
      behavior.updateAndGet(current -> new MintBehavior(current.insideMint, failure));
    }

    void stopFailing() {
      behavior.updateAndGet(current -> new MintBehavior(current.insideMint, null));
    }

    @Override
    public OAuthToken mint() {
      peakConcurrentMints.accumulateAndGet(threadsInsideMint.incrementAndGet(), Math::max);
      try {
        var n = mintCount.incrementAndGet();

        var currentBehavior = behavior.get();
        currentBehavior.insideMint.run();

        if (currentBehavior.failure != null) {
          sneakyThrow(currentBehavior.failure);
        }

        var issuedAt = mintClock.instant();
        return new OAuthToken("tok-" + n, issuedAt, issuedAt.plus(LIFETIME));
      } finally {
        threadsInsideMint.decrementAndGet();
      }
    }
  }

  private static final class MintBehavior {
    private final Runnable insideMint;
    private final Throwable failure;

    MintBehavior(Runnable insideMint, Throwable failure) {
      this.insideMint = insideMint;
      this.failure = failure;
    }
  }

  /*A clock the test moves by hand, to drive a token fresh -> stale -> expired with no waiting. */
  private static final class MutableClock extends Clock {
    private final AtomicReference<Instant> now;
    private final CountDownLatch stoppedAfterTokenSnapshot = new CountDownLatch(1);
    private final CountDownLatch resumeStoppedThread = new CountDownLatch(1);
    private final CountDownLatch expiredSnapshotRejected = new CountDownLatch(1);
    private final AtomicInteger stoppedThreadClockReads = new AtomicInteger();
    private volatile Thread stoppedThread;

    MutableClock(Instant start) {
      this.now = new AtomicReference<>(start);
    }

    void advance(Duration delta) {
      now.updateAndGet(instant -> instant.plus(delta));
    }

    /**
     * Stops {@code thread} when it next checks token freshness, after it has captured its token
     * snapshot, but before it can elect or join a refresh round.
     */
    void stopThreadAtNextFreshnessCheck(Thread thread) {
      stoppedThread = thread;
    }

    void awaitThreadStoppedAfterTokenSnapshot() {
      awaitLatch(stoppedAfterTokenSnapshot);
    }

    void resumeStoppedThread() {
      resumeStoppedThread.countDown();
    }

    /** Blocks until the stopped thread re-checks and rejects its expired token snapshot. */
    void awaitExpiredSnapshotRejected() {
      awaitLatch(expiredSnapshotRejected);
    }

    @Override
    public Instant instant() {
      if (Thread.currentThread() == stoppedThread) {
        int read = stoppedThreadClockReads.incrementAndGet();
        if (read == 1) {
          stoppedAfterTokenSnapshot.countDown();
          awaitLatch(resumeStoppedThread);
        } else if (read == 2) {
          expiredSnapshotRejected.countDown();
        }
      }
      return now.get();
    }

    @Override
    public ZoneId getZone() {
      return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zone) {
      return this;
    }
  }
}
