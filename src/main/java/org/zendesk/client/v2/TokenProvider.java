package org.zendesk.client.v2;

/**
 * Supplies a currently-usable bearer token. Called on every request from many threads at once, so
 * implementations must be thread-safe and may block while a token is minted.
 *
 * @since FIXME
 */
interface TokenProvider {

  /**
   * @return a token that is valid at the moment of return
   * @throws ZendeskOAuthException if no usable token exists and minting one fails
   */
  String provideBearerToken();
}
