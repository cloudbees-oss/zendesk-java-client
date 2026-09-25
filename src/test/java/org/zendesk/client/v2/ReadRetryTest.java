package org.zendesk.client.v2;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

/**
 * Tests classification, bounds, and interruption of synchronous read retries.
 *
 * @since FIXME
 */
public class ReadRetryTest {
  @Test
  /** Verifies transient HTTP and transport errors have at most three attempts. */
  public void retriesTransientHttpAndTransportAtMostThreeTimes() {
    List<ZendeskException> failures = new ArrayList<>();
    for (int status : new int[] {500, 502, 503, 504}) {
      failures.add(new ZendeskResponseException(status, "test", ""));
    }
    for (Throwable cause :
        Arrays.asList(
            new ConnectException(),
            new SocketException("reset"),
            new SocketTimeoutException(),
            new TimeoutException(),
            new java.nio.channels.ClosedChannelException()))
      failures.add(new ZendeskException(cause));
    for (ZendeskException failure : failures) {
      AtomicInteger attempts = new AtomicInteger();
      List<Long> delays = new ArrayList<>();
      assertSame(
          failure,
          assertThrows(
              ZendeskException.class,
              () ->
                  ReadRetry.execute(
                      () -> {
                        attempts.incrementAndGet();
                        throw failure;
                      },
                      delays::add)));
      assertEquals(3, attempts.get());
      assertEquals(Arrays.asList(1000L, 2000L), delays);
    }
  }

  @Test
  /** Verifies a successful retry returns immediately without extra attempts. */
  public void succeedsOnNextReadWithoutExtraAttempts() {
    AtomicInteger attempts = new AtomicInteger();
    assertEquals(
        "ok",
        ReadRetry.execute(
            () -> {
              if (attempts.incrementAndGet() == 1)
                throw new ZendeskException(new ConnectException());
              return "ok";
            },
            millis -> {}));
    assertEquals(2, attempts.get());
  }

  @Test
  /** Verifies permanent, parsing, and OAuth failures are not retried. */
  public void doesNotRetryPermanentFailuresParsingOrOAuthMinting() {
    for (ZendeskException failure :
        Arrays.asList(
            new ZendeskResponseException(401, "test", ""),
            new ZendeskResponseException(403, "test", ""),
            new ZendeskResponseException(404, "test", ""),
            new ZendeskResponseException(422, "test", ""),
            new ZendeskResponseException(501, "test", ""),
            new ZendeskException(new IOException("parse")),
            new ZendeskOAuthException("mint failed", new TimeoutException()))) {
      AtomicInteger attempts = new AtomicInteger();
      assertSame(
          failure,
          assertThrows(
              ZendeskException.class,
              () ->
                  ReadRetry.execute(
                      () -> {
                        attempts.incrementAndGet();
                        throw failure;
                      },
                      millis -> fail("No backoff expected"))));
      assertEquals(1, attempts.get());
    }
  }

  @Test
  /** Verifies interruption stops retrying and preserves the interrupt flag. */
  public void interruptionStopsImmediatelyAndPreservesFlag() {
    AtomicInteger attempts = new AtomicInteger();
    try {
      assertThrows(
          ZendeskException.class,
          () ->
              ReadRetry.execute(
                  () -> {
                    attempts.incrementAndGet();
                    throw new ZendeskException(new ConnectException());
                  },
                  millis -> {
                    throw new InterruptedException();
                  }));
      assertTrue(Thread.currentThread().isInterrupted());
      assertEquals(1, attempts.get());
    } finally {
      Thread.interrupted();
    }
  }
}
