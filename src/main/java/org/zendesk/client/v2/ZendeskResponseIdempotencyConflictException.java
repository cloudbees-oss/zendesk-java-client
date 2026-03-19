package org.zendesk.client.v2;

import java.io.IOException;
import org.asynchttpclient.Response;

public class ZendeskResponseIdempotencyConflictException extends ZendeskResponseException {

  private static final long serialVersionUID = 1L;

  public ZendeskResponseIdempotencyConflictException(Response res) throws IOException {
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

  @Override
  public boolean isIdempotencyConflict() {
    return true;
  }
}
