package org.zendesk.client.v2;

import java.time.Instant;

/**
 * An immutable OAuth access token with the instants it was issued and expires.
 *
 * @since 1.6.0
 */
final class OAuthToken {

  /** Bearer token value. */
  private final String accessToken;

  /** Instant from which the client treats this token as issued. */
  private final Instant issuedAt;

  /** Instant at which the token must no longer be served. */
  private final Instant expiresAt;

  /**
   * Creates an OAuth access token with its local issue and expiry instants.
   *
   * @param accessToken bearer token value
   * @param issuedAt instant from which the client treats this token as issued
   * @param expiresAt instant at which the token must no longer be served
   */
  OAuthToken(String accessToken, Instant issuedAt, Instant expiresAt) {
    this.accessToken = accessToken;
    this.issuedAt = issuedAt;
    this.expiresAt = expiresAt;
  }

  /**
   * Returns the bearer token value.
   *
   * @return bearer token value
   */
  String accessToken() {
    return accessToken;
  }

  /**
   * Returns the issue instant.
   *
   * @return issue instant
   */
  Instant issuedAt() {
    return issuedAt;
  }

  /**
   * Returns the expiry instant.
   *
   * @return expiry instant
   */
  Instant expiresAt() {
    return expiresAt;
  }

  /** Does not include the access token so logging a token cannot leak the credential. */
  @Override
  public String toString() {
    return "OAuthToken{issuedAt=" + issuedAt + ", expiresAt=" + expiresAt + '}';
  }
}
