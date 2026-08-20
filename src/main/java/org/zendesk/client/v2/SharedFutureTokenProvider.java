package org.zendesk.client.v2;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Caches an access token and keeps it fresh, allowing only one mint in flight at a time. A fresh
 * token is read lock-free; a stale but unexpired one keeps being served while one thread refreshes
 * it; threads left with no servable token await the shared mint attempt.
 *
 * <p>Refresh is single-flight but synchronous: the elected thread performs the mint before
 * returning. If the cached token is stale but still servable, that thread refreshes it while other
 * callers may continue using the cached token. If no servable token exists, the elected thread
 * always attempts to mint. After a mint failure, the provider backs off from further refresh
 * attempts only while a servable cached token remains.
 *
 * <p>Two invariants a future change must preserve:
 *
 * <ol>
 *   <li>The {@code synchronized} block decides only who mints; the mint runs outside it, so threads
 *       never queue behind a network round trip.
 *   <li>Both the leader and awaiter recovery paths re-check the cached token against the live clock
 *       via {@link #isServable} before giving up. A failed refresh round does not cascade failures
 *       if the cache contains a servable token, and a token that expires <em>during</em> a mint is
 *       not handed out.
 * </ol>
 *
 * @since FIXME
 */
final class SharedFutureTokenProvider implements TokenProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(SharedFutureTokenProvider.class);
  private static final Duration REFRESH_BACKOFF_DURATION = Duration.ofSeconds(10);

  private final TokenMinter minter;
  private final Clock clock;
  private final double refreshThreshold;

  /** Read lock-free on the hot path; the token is immutable, so publication is safe. */
  private final AtomicReference<OAuthToken> currentToken = new AtomicReference<>();

  /**
   * Private monitor guarding {@link #inFlightRefresh}, so nothing outside this class can stall or
   * deadlock a refresh round. Held only long enough to elect a leader and not during mint.
   */
  private final Object lock = new Object();

  /** Null when no refresh is in progress. Guarded by {@link #lock}. */
  private CompletableFuture<OAuthToken> inFlightRefresh;

  /* Instant until when mint refreshes should backoff on failures if there is a servable token. */
  private volatile Instant backOffRefreshUntil = Instant.MIN;

  /**
   * Creates a provider backed by a single-flight token minter.
   *
   * @param minter mints tokens when the cache is empty, expired, or past the refresh threshold
   * @param clock used to make freshness and expiry decisions
   * @param refreshThreshold fraction of token lifetime remaining below which refresh begins
   * @throws IllegalArgumentException if {@code refreshThreshold} is not between 0 and 1 exclusive
   */
  SharedFutureTokenProvider(TokenMinter minter, Clock clock, double refreshThreshold) {
    if (!(refreshThreshold > 0.0 && refreshThreshold < 1.0)) {
      throw new IllegalArgumentException(
          "refreshThreshold must be between 0 and 1 exclusive, but was " + refreshThreshold);
    }
    this.minter = minter;
    this.clock = clock;
    this.refreshThreshold = refreshThreshold;
  }

  /** {@inheritDoc} */
  @Override
  public String provideBearerToken() {
    OAuthToken token = currentToken.get();
    if (isFresh(token)) {
      return token.accessToken();
    }

    // Elect the refreshing thread.
    CompletableFuture<OAuthToken> refreshRound;
    boolean isLeader;
    synchronized (lock) {
      if (inFlightRefresh != null) {
        refreshRound = inFlightRefresh;
        isLeader = false;
      } else {
        inFlightRefresh = new CompletableFuture<>();
        refreshRound = inFlightRefresh;
        isLeader = true;
      }
    }

    if (isLeader) {
      return mintAsLeader(refreshRound);
    } else if (isServable(token)) {
      return token.accessToken();
    } else {
      return await(refreshRound);
    }
  }

  private String mintAsLeader(CompletableFuture<OAuthToken> refreshRound) {
    try {
      // If another thread has already refreshed, just use that result.
      OAuthToken cached = currentToken.get();
      if (isFresh(cached)) {
        refreshRound.complete(cached);
        return cached.accessToken();
      }

      if (isServable(cached) && shouldBackOffRefresh()) {
        refreshRound.complete(cached);
        return cached.accessToken();
      }

      // Ensure we publish the token before completing the round.
      OAuthToken minted = minter.mint();
      LOGGER.debug("Minted a new OAuth access token expiring at {}", minted.expiresAt());

      currentToken.set(minted);
      refreshRound.complete(minted);
      return minted.accessToken();
    } catch (RuntimeException e) {
      backOffRefreshUntil = clock.instant().plus(REFRESH_BACKOFF_DURATION);
      refreshRound.completeExceptionally(e);

      OAuthToken cached = currentToken.get();
      if (isServable(cached)) {
        LOGGER.debug("OAuth access token mint failure", e);
        LOGGER.warn(
            "Failed to mint an OAuth access token. Serving the cached token expiring at {}",
            cached.expiresAt());
        return cached.accessToken();
      }

      LOGGER.warn("Failed to mint an OAuth access token. No servable cached token remains", e);
      throw e;
    } finally {
      // Clear out the slot so that the next refresh can run when needed, and ensure
      // that the round completes if no other path has done it yet. Otherwise, its
      // waiting threads will block indefinitely.
      synchronized (lock) {
        inFlightRefresh = null;
      }

      if (!refreshRound.isDone()) {
        refreshRound.completeExceptionally(
            new ZendeskOAuthException("OAuth token refresh did not complete"));
      }
    }
  }

  private String await(CompletableFuture<OAuthToken> refreshRound) {
    try {
      return refreshRound.get().accessToken();
    } catch (ExecutionException e) {
      OAuthToken cached = currentToken.get();
      if (isServable(cached)) {
        return cached.accessToken();
      }

      Throwable cause = e.getCause();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      }

      throw new ZendeskOAuthException("Failed to obtain an OAuth access token", cause);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ZendeskOAuthException("Interrupted while awaiting an OAuth access token", e);
    }
  }

  private boolean isFresh(OAuthToken token) {
    if (token == null) {
      return false;
    }

    long lifetimeMillis = Duration.between(token.issuedAt(), token.expiresAt()).toMillis();
    long remainingMillis = Duration.between(clock.instant(), token.expiresAt()).toMillis();
    return remainingMillis > (long) (lifetimeMillis * refreshThreshold);
  }

  private boolean isServable(OAuthToken token) {
    return token != null && clock.instant().isBefore(token.expiresAt());
  }

  private boolean shouldBackOffRefresh() {
    return clock.instant().isBefore(backOffRefreshUntil);
  }
}
