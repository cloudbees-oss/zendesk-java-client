package org.zendesk.client.v2;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * Bounded synchronous retries, used only at explicitly selected read boundaries.
 *
 * @since FIXME
 */
final class ReadRetry {
  private ReadRetry() {}

  /**
   * Interruptible wait strategy, replaceable in tests.
   *
   * @since FIXME
   */
  interface Sleeper {
    /** Waits for the specified number of milliseconds. */
    void sleep(long millis) throws InterruptedException;
  }

  /** Executes a read with at most three attempts; propagates permanent failures unchanged. */
  static <T> T execute(Supplier<T> operation, Sleeper sleeper) {
    for (int attempt = 1; ; attempt++) {
      try {
        return operation.get();
      } catch (ZendeskException failure) {
        long delay = delayMillis(failure, attempt);
        if (attempt >= 3 || delay < 0 || Thread.currentThread().isInterrupted()) throw failure;
        try {
          sleeper.sleep(delay);
        } catch (InterruptedException interrupted) {
          Thread.currentThread().interrupt();
          throw new ZendeskException("Interrupted during Zendesk read backoff", interrupted);
        }
      }
    }
  }

  private static long delayMillis(ZendeskException failure, int attempt) {
    if (failure instanceof ZendeskOAuthException) return -1;
    if (failure instanceof ZendeskResponseRateLimitException) {
      long seconds = ((ZendeskResponseRateLimitException) failure).getRetryAfter();
      // Do not retry earlier than the server asked, or park a scheduler indefinitely.
      return seconds >= 0 && seconds <= 60 ? seconds * 1000 : -1;
    }
    if (failure instanceof ZendeskResponseException) {
      int status = ((ZendeskResponseException) failure).getStatusCode();
      return status == 500 || status == 502 || status == 503 || status == 504
          ? attempt * 1000L
          : -1;
    }
    for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
      if (cause instanceof InterruptedException) return -1;
      if (cause instanceof SocketException
          || cause instanceof SocketTimeoutException
          || cause instanceof ClosedChannelException
          || cause instanceof TimeoutException
          || cause instanceof org.asynchttpclient.exception.RemotelyClosedException) {
        return attempt * 1000L;
      }
    }
    return -1;
  }
}
