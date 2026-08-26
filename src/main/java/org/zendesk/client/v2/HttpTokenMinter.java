package org.zendesk.client.v2;

import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;

/**
 * Mints access tokens with the OAuth {@code client_credentials} grant.
 *
 * <p>Bypasses {@code Zendesk.reqBuilder} because this endpoint is at the host root, not under
 * {@code /api/v2}, and must not authenticate the call that produces its own credentials. Bypasses
 * the shared body logging because the request carries the client secret and the response the access
 * token; neither this class nor the exceptions it throws logs either.
 *
 * <p>Validates the token response by type rather than coercing through {@code asText}/{@code
 * asLong} as the pagination handlers in {@code Zendesk} do. A malformed pagination field degrades
 * benignly, since pagination just stops, but a malformed lifetime can corrupt the refresh schedule
 * for every request that follows. Jackson coerces rather than rejects ({@code true} would read as a
 * one-second lifetime, a non-numeric string as zero), so the type checks make a bad grant a loud
 * {@link ZendeskOAuthException} instead of propagating a potentially incorrect value.
 *
 * @see <a href="https://developer.zendesk.com/api-reference/ticketing/oauth/grant_type_tokens/">
 *     OAuth grant type tokens</a>
 * @since 1.6.0
 */
final class HttpTokenMinter implements TokenMinter {

  private static final String GRANT_TYPE = "client_credentials";
  private static final String OAUTH_TOKEN_PATH = "/oauth/tokens";
  private static final String EXPIRES_IN = "expires_in";
  private static final String ACCESS_TOKEN = "access_token";

  private final AsyncHttpClient client;
  private final String tokenUrl;
  private final String clientId;
  private final String clientSecret;
  private final String scope;
  private final int requestedLifetimeSeconds;
  private final Clock clock;

  /**
   * Isolated mapper for OAuth request and response bodies. Source-in-location is disabled so parse
   * errors cannot include token-bearing response bodies.
   */
  private final ObjectMapper mapper =
      JsonMapper.builder().disable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION).build();

  /**
   * Creates a token minter for one OAuth client.
   *
   * @param client HTTP client used to call Zendesk
   * @param baseHostUrl Zendesk host URL without the {@code /api/v2} suffix
   * @param clientId OAuth client identifier
   * @param clientSecret OAuth client secret
   * @param scope space-separated OAuth scopes to request
   * @param requestedLifetimeSeconds requested token lifetime in seconds
   * @param clock clock used to compute token issue and expiry instants
   */
  HttpTokenMinter(
      AsyncHttpClient client,
      String baseHostUrl,
      String clientId,
      String clientSecret,
      String scope,
      int requestedLifetimeSeconds,
      Clock clock) {
    this.client = client;
    this.tokenUrl = baseHostUrl + OAUTH_TOKEN_PATH;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.scope = scope;
    this.requestedLifetimeSeconds = requestedLifetimeSeconds;
    this.clock = clock;
  }

  /** {@inheritDoc} */
  @Override
  public OAuthToken mint() {
    Request request = buildRequest();

    // Anchor issue time before the call, so the computed expiry skews early rather than late.
    Instant issuedAt = clock.instant();

    Response response = execute(request);

    int statusCode = response.getStatusCode();
    if (statusCode < 200 || statusCode >= 300) {
      throw new ZendeskOAuthException(
          "Failed to mint an OAuth access token: HTTP/"
              + statusCode
              + " "
              + response.getStatusText());
    }

    return parseToken(response, issuedAt);
  }

  private Request buildRequest() {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("grant_type", GRANT_TYPE);
    body.put("client_id", clientId);
    body.put("client_secret", clientSecret);
    body.put("scope", scope);

    // Always set so the token lifetime is ours rather than the server default.
    body.put(EXPIRES_IN, requestedLifetimeSeconds);

    byte[] serialized;
    try {
      serialized = mapper.writeValueAsBytes(body);
    } catch (IOException e) {
      throw new ZendeskOAuthException("Failed to serialize the OAuth token request", e);
    }

    return new RequestBuilder("POST")
        .setUrl(tokenUrl)
        .addHeader("Content-Type", "application/json")
        .setBody(serialized)
        .build();
  }

  private Response execute(Request request) {
    try {
      return client.executeRequest(request).get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ZendeskOAuthException("Interrupted while minting an OAuth access token", e);
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      throw new ZendeskOAuthException(
          "Failed to mint an OAuth access token", cause != null ? cause : e);
    }
  }

  private OAuthToken parseToken(Response response, Instant issuedAt) {
    JsonNode parsed;
    try {
      parsed = mapper.readTree(response.getResponseBodyAsStream());
    } catch (IOException e) {
      throw new ZendeskOAuthException("Failed to parse the OAuth token response", e);
    }

    if (parsed == null || parsed.isMissingNode()) {
      throw new ZendeskOAuthException("OAuth token response was empty");
    } else if (!parsed.isObject()) {
      throw new ZendeskOAuthException("OAuth token response was not a JSON object");
    }

    JsonNode accessToken = parsed.get(ACCESS_TOKEN);
    if (accessToken == null || !accessToken.isTextual() || accessToken.asText().trim().isEmpty()) {
      throw new ZendeskOAuthException("OAuth token response had no usable access_token");
    }

    return new OAuthToken(
        accessToken.asText(), issuedAt, issuedAt.plusSeconds(resolveLifetimeSeconds(parsed)));
  }

  private long resolveLifetimeSeconds(JsonNode parsed) {
    JsonNode grantedLifetime = parsed.get(EXPIRES_IN);

    // Fallback to the requested expiry only if Zendesk did not return one.
    if (grantedLifetime == null || grantedLifetime.isNull()) {
      return requestedLifetimeSeconds;
    }

    if (!grantedLifetime.isIntegralNumber()) {
      throw new ZendeskOAuthException(
          "OAuth token response had a non-integer expires_in of type "
              + grantedLifetime.getNodeType());
    } else if (!grantedLifetime.canConvertToLong()) {
      throw new ZendeskOAuthException(
          "OAuth token response reported an out-of-range expires_in of "
              + grantedLifetime.asText());
    }

    // Guard against unusable grants: a token born expired would mean re-minting on every request,
    // and one beyond the documented maximum is not credible. The upper bound is intentionally
    // inclusive here even though Builder.build() rejects a requested value of MAX. This means we
    // are strict in what we ask for, but liberal in what the server hands back.
    long granted = grantedLifetime.asLong();
    if (granted <= 0 || Zendesk.Builder.MAX_OAUTH_TOKEN_LIFETIME_SECONDS < granted) {
      throw new ZendeskOAuthException(
          "OAuth token response reported an out-of-range expires_in of " + granted);
    }

    return granted;
  }
}
