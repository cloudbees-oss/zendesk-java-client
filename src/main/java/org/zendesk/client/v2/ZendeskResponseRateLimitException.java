package org.zendesk.client.v2;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
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
              java.time.Duration.between(java.time.Instant.now(), parseHttpDate(value)).toMillis();
          this.retryAfter = millis <= 0 ? 0L : 1 + (millis - 1) / 1000;
        } catch (java.time.DateTimeException | ArithmeticException invalid) {
          // An unusable header is not permission to retry earlier than requested.
          this.retryAfter = Long.MAX_VALUE;
        }
      }
    }
  }

  private static Instant parseHttpDate(String value) {
    try {
      return ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
    } catch (DateTimeException invalid) {
      // HTTP recipients must also accept RFC 850 and ANSI C asctime dates (RFC 9110).
      for (String pattern :
          new String[] {"EEEE, dd-MMM-yy HH:mm:ss 'GMT'", "EEE MMM d HH:mm:ss yyyy"}) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        format.setLenient(false);
        // A two-digit year more than 50 years ahead denotes the previous century.
        format.set2DigitYearStart(
            Date.from(ZonedDateTime.now(ZoneOffset.UTC).minusYears(50).toInstant()));
        ParsePosition position = new ParsePosition(0);
        Date parsed = format.parse(value, position);
        if (parsed != null && position.getIndex() == value.length()) {
          return parsed.toInstant();
        }
      }
      throw invalid;
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
