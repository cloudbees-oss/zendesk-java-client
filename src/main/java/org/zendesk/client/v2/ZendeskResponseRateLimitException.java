package org.zendesk.client.v2;

import org.asynchttpclient.Response;

public class ZendeskResponseRateLimitException extends ZendeskResponseException {

  private static final long serialVersionUID = 1L;
  private static final String RETRY_AFTER_HEADER = "Retry-After";
  private static final long DEFAULT_RETRY_AFTER = 60L;

  private Long retryAfter = DEFAULT_RETRY_AFTER;

  public ZendeskResponseRateLimitException(Response resp) {
    super(resp);
    try {
      this.retryAfter = Long.valueOf(resp.getHeader(RETRY_AFTER_HEADER));
    } catch (NumberFormatException e) {
      // HTTP also permits an absolute date. Round up: never retry before that instant.
      String value = resp.getHeader(RETRY_AFTER_HEADER);
      if (value != null) {
        try {
          long millis =
              java.time.Duration.between(
                      java.time.Instant.now(),
                      java.time.ZonedDateTime.parse(
                              value, java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME)
                          .toInstant())
                  .toMillis();
          this.retryAfter = millis <= 0 ? 0L : 1 + (millis - 1) / 1000;
        } catch (java.time.DateTimeException | ArithmeticException invalid) {
          // An unusable header is not permission to retry earlier than requested.
          this.retryAfter = Long.MAX_VALUE;
        }
      }
    }
  }

  protected ZendeskResponseRateLimitException(ZendeskResponseRateLimitException e) {
    super(e);
    this.retryAfter = e.getRetryAfter();
  }

  public Long getRetryAfter() {
    return retryAfter;
  }
}
