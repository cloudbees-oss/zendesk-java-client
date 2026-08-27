package org.zendesk.client.v2;

/**
 * {@link ZendeskException} for failures to obtain an OAuth access token.
 *
 * <p>Distinct from {@link ZendeskResponseException} so callers can tell authentication issues apart
 * from actual API exceptions.
 *
 * @since 1.6.0
 */
public class ZendeskOAuthException extends ZendeskException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates an OAuth exception with a message.
   *
   * @param message exception message
   */
  public ZendeskOAuthException(String message) {
    super(message);
  }

  /**
   * Creates an OAuth exception with a message and cause.
   *
   * @param message exception message
   * @param cause underlying failure
   */
  public ZendeskOAuthException(String message, Throwable cause) {
    super(message, cause);
  }
}
