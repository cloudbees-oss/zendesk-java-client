package org.zendesk.client.v2;

import java.io.Closeable;
import java.io.IOException;
import java.time.Clock;
import java.util.Objects;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link TokenProvider} that mints and refreshes access tokens with the OAuth {@code
 * client_credentials} grant.
 *
 * <p>Thread-safe. {@link #build() Building} performs no network I/O; the first call to {@link
 * #provideBearerToken()} mints synchronously.
 *
 * @since 1.6.1
 */
public final class ClientCredentialsTokenProvider implements TokenProvider, Closeable {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ClientCredentialsTokenProvider.class);

  private static final int MIN_TOKEN_LIFETIME_SECONDS = 300;

  private final TokenProvider delegate;
  private final AsyncHttpClient client;
  private final boolean closeClient;
  private volatile boolean closed = false;

  private ClientCredentialsTokenProvider(
      TokenProvider delegate, AsyncHttpClient client, boolean closeClient) {
    this.delegate = delegate;
    this.client = client;
    this.closeClient = closeClient;
  }

  /**
   * Starts building a provider for the given Zendesk instance.
   *
   * @param url the Zendesk host URL, for example {@code https://example.zendesk.com}
   * @return a new builder
   */
  public static Builder builder(String url) {
    return new Builder(url);
  }

  /**
   * {@inheritDoc}
   *
   * @throws ZendeskOAuthException if no usable token exists and minting one fails
   * @throws IllegalStateException if this provider is closed
   */
  @Override
  public String provideBearerToken() {
    if (closed) {
      throw new IllegalStateException("ClientCredentialsTokenProvider is closed");
    }
    return delegate.provideBearerToken();
  }

  /**
   * Stops serving tokens and closes the HTTP client used to mint them, unless it was supplied
   * through {@link Builder#setClient(AsyncHttpClient)}. {@link Zendesk} clients sharing this
   * provider never close it; its creator does.
   */
  @Override
  public void close() {
    closed = true;
    if (closeClient && !client.isClosed()) {
      try {
        client.close();
      } catch (IOException e) {
        LOGGER.warn("Unexpected error on client close", e);
      }
    }
  }

  /** Builder for {@link ClientCredentialsTokenProvider}. */
  public static final class Builder {
    private final String url;
    private AsyncHttpClient client = null;
    private String clientId = null;
    private String clientSecret = null;
    private String scope = null;
    private int tokenLifetimeSeconds = Zendesk.Builder.DEFAULT_OAUTH_TOKEN_LIFETIME_SECONDS;
    private double refreshThreshold = Zendesk.Builder.DEFAULT_OAUTH_REFRESH_THRESHOLD;

    private Builder(String url) {
      this.url = url;
    }

    /**
     * HTTP client used to call the token endpoint. The provider does not close a supplied client.
     * When unset, the provider creates its own and closes it in {@link
     * ClientCredentialsTokenProvider#close()}.
     *
     * @param client the HTTP client to use
     * @return this builder instance
     */
    public Builder setClient(AsyncHttpClient client) {
      this.client = client;
      return this;
    }

    /**
     * OAuth client credentials. All arguments must be non-null and nonblank, validated by {@link
     * #build()}.
     *
     * @param clientId the OAuth client's unique identifier
     * @param clientSecret the OAuth client's secret
     * @param scope space-separated scopes to request, for example {@code "tickets:read"}
     * @return this builder instance
     */
    public Builder setClientCredentials(String clientId, String clientSecret, String scope) {
      this.clientId = clientId;
      this.clientSecret = clientSecret;
      this.scope = scope;
      return this;
    }

    /**
     * Requested lifetime for minted access tokens, in seconds, defaulting to {@link
     * Zendesk.Builder#DEFAULT_OAUTH_TOKEN_LIFETIME_SECONDS}. Must be strictly between 300 (5
     * minutes) and 172,800 (2 days), validated by {@link #build()}. Zendesk may grant a shorter
     * lifetime than requested, in which case the granted one is honored.
     *
     * @param tokenLifetimeSeconds seconds a minted token should remain valid
     * @return this builder instance
     */
    public Builder setTokenLifetimeSeconds(int tokenLifetimeSeconds) {
      this.tokenLifetimeSeconds = tokenLifetimeSeconds;
      return this;
    }

    /**
     * Fraction of a token's lifetime that may remain before it is refreshed, defaulting to {@link
     * Zendesk.Builder#DEFAULT_OAUTH_REFRESH_THRESHOLD}. Must be strictly between 0 and 1, validated
     * by {@link #build()}.
     *
     * @param refreshThreshold fraction of the lifetime that may remain before refreshing
     * @return this builder instance
     */
    public Builder setRefreshThreshold(double refreshThreshold) {
      this.refreshThreshold = refreshThreshold;
      return this;
    }

    /**
     * Builds the provider without performing any network I/O.
     *
     * @return a new provider
     * @throws NullPointerException if the URL or a credential is null
     * @throws IllegalArgumentException if a credential is blank, or the lifetime or refresh
     *     threshold is out of range
     */
    public ClientCredentialsTokenProvider build() {
      validate();
      boolean closeClient = client == null;
      AsyncHttpClient httpClient =
          closeClient
              ? new DefaultAsyncHttpClient(Zendesk.DEFAULT_ASYNC_HTTP_CLIENT_CONFIG)
              : client;
      String baseHostUrl = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
      TokenProvider delegate =
          new SharedFutureTokenProvider(
              new HttpTokenMinter(
                  httpClient,
                  baseHostUrl,
                  clientId,
                  clientSecret,
                  scope,
                  tokenLifetimeSeconds,
                  Clock.systemUTC()),
              Clock.systemUTC(),
              refreshThreshold);
      return new ClientCredentialsTokenProvider(delegate, httpClient, closeClient);
    }

    void validate() {
      Objects.requireNonNull(url, "Zendesk URL cannot be null");
      requireNonBlank(clientId, "OAuth client id");
      requireNonBlank(clientSecret, "OAuth client secret");
      requireNonBlank(scope, "OAuth scope");
      if (tokenLifetimeSeconds <= MIN_TOKEN_LIFETIME_SECONDS
          || tokenLifetimeSeconds >= Zendesk.Builder.MAX_OAUTH_TOKEN_LIFETIME_SECONDS) {
        throw new IllegalArgumentException(
            "OAuth token lifetime must be between "
                + MIN_TOKEN_LIFETIME_SECONDS
                + " and "
                + Zendesk.Builder.MAX_OAUTH_TOKEN_LIFETIME_SECONDS
                + " seconds exclusive, but was "
                + tokenLifetimeSeconds);
      }
      if (!(refreshThreshold > 0.0 && refreshThreshold < 1.0)) {
        throw new IllegalArgumentException(
            "OAuth refresh threshold must be between 0 and 1 exclusive, but was "
                + refreshThreshold);
      }
    }

    private static void requireNonBlank(String value, String name) {
      Objects.requireNonNull(value, name + " cannot be null");
      if (value.trim().isEmpty()) {
        throw new IllegalArgumentException(name + " cannot be blank");
      }
    }
  }
}
