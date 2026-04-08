package org.zendesk.client.v2;

import org.asynchttpclient.Response;

/**
 * Exception thrown when the Zendesk API returns an idempotency conflict error.
 *
 * <p>This exception is thrown when a request is retried with the same idempotency key but different
 * request parameters. The API returns a 400 status code with {@code error:
 * "IdempotentRequestError"} to indicate that the request parameters don't match the original
 * request associated with the idempotency key.
 *
 * <p>To resolve this error, either use a new idempotency key or ensure the request parameters match
 * the original request.
 *
 * @see <a href="https://developer.zendesk.com/api-reference/ticketing/introduction/#idempotency">
 *     Zendesk API Idempotency</a>
 * @since 1.5.0
 */
public class ZendeskResponseIdempotencyConflictException extends ZendeskResponseException {

  private static final long serialVersionUID = 1L;

  public ZendeskResponseIdempotencyConflictException(Response res) {
    super(res);
  }

  public ZendeskResponseIdempotencyConflictException(
      int statusCode, String statusText, String body) {
    super(statusCode, statusText, body);
  }

  public ZendeskResponseIdempotencyConflictException(
      ZendeskResponseIdempotencyConflictException cause) {
    super(cause);
  }
}
