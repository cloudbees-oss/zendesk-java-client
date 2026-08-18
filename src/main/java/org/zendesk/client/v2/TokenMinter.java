package org.zendesk.client.v2;

/**
 * Acquires a brand-new access token. Does no caching, no coordination and no retry.
 *
 * @since FIXME
 */
interface TokenMinter {

  /**
   * @return a freshly minted access token
   * @throws ZendeskOAuthException if a token could not be minted
   */
  OAuthToken mint();
}
