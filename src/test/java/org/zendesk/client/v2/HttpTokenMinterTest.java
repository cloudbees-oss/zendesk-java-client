package org.zendesk.client.v2;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.apache.commons.text.RandomStringGenerator;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

public class HttpTokenMinterTest {

  private static final RandomStringGenerator RANDOM_STRING_GENERATOR =
      new RandomStringGenerator.Builder().withinRange('a', 'z').build();
  private static final String CLIENT_ID = RANDOM_STRING_GENERATOR.generate(12);
  private static final String CLIENT_SECRET = RANDOM_STRING_GENERATOR.generate(24);
  private static final String SCOPE = "tickets:read users:read";
  private static final String ACCESS_TOKEN = RANDOM_STRING_GENERATOR.generate(30);
  private static final int LIFETIME_SECONDS = 1800;

  @ClassRule
  public static WireMockClassRule zendeskApiClass =
      new WireMockClassRule(options().dynamicPort().dynamicHttpsPort());

  @Rule public WireMockClassRule zendeskApiMock = zendeskApiClass;

  private AsyncHttpClient httpClient;
  private String baseHostUrl;

  @Before
  public void setUp() {
    httpClient = new DefaultAsyncHttpClient();
    baseHostUrl = String.format("http://localhost:%d", zendeskApiMock.port());
  }

  @After
  public void tearDown() throws Exception {
    httpClient.close();
  }

  @Test
  public void mintPostsClientCredentialsGrantToHostRoot() {
    stubTokenResponse(ok(tokenBody(ACCESS_TOKEN, LIFETIME_SECONDS)));

    var token = minter().mint();

    assertThat(token.accessToken()).isEqualTo(ACCESS_TOKEN);

    zendeskApiMock.verify(
        postRequestedFor(urlPathEqualTo("/oauth/tokens"))
            .withHeader("Content-Type", equalTo("application/json"))
            .withRequestBody(
                equalToJson(
                    "{\"grant_type\":\"client_credentials\","
                        + "\"client_id\":\""
                        + CLIENT_ID
                        + "\","
                        + "\"client_secret\":\""
                        + CLIENT_SECRET
                        + "\","
                        + "\"scope\":\""
                        + SCOPE
                        + "\","
                        + "\"expires_in\":"
                        + LIFETIME_SECONDS
                        + "}")));
  }

  @Test
  public void mintAlwaysSendsExpiresIn() {
    stubTokenResponse(ok(tokenBody(ACCESS_TOKEN, LIFETIME_SECONDS)));

    minter(600).mint();

    zendeskApiMock.verify(
        postRequestedFor(urlPathEqualTo("/oauth/tokens"))
            .withRequestBody(matchingJsonPath("$.expires_in", equalTo("600"))));
  }

  @Test
  public void mintFallsBackToRequestedLifetimeWhenExpiresInAbsentOrNull() {
    String[] absent = {
      "{\"access_token\":\"" + ACCESS_TOKEN + "\"}",
      "{\"access_token\":\"" + ACCESS_TOKEN + "\",\"expires_in\":null}"
    };

    for (String body : absent) {
      zendeskApiMock.resetAll();
      stubTokenResponse(ok(body));

      var token = minter().mint();

      assertThat(token.expiresAt())
          .as("body %s", body)
          .isEqualTo(token.issuedAt().plusSeconds(LIFETIME_SECONDS));
    }
  }

  @Test
  public void mintHonorsGrantedLifetimeOverRequested() {
    stubTokenResponse(ok(tokenBody(ACCESS_TOKEN, 900)));

    var token = minter().mint();

    assertThat(token.expiresAt()).isEqualTo(token.issuedAt().plusSeconds(900));
  }

  @Test
  public void mintAnchorsExpiryBeforeRequest() {
    var t0 = Instant.parse("2026-01-01T00:00:00Z");

    stubTokenResponse(ok(tokenBody(ACCESS_TOKEN, LIFETIME_SECONDS)));

    var token = minter(Clock.fixed(t0, ZoneOffset.UTC)).mint();

    assertThat(token.issuedAt()).isEqualTo(t0);
    assertThat(token.expiresAt()).isEqualTo(t0.plusSeconds(LIFETIME_SECONDS));
  }

  @Test
  public void mintNonSuccessStatusExpectException() {
    int[] statuses = {400, 401, 403, 429, 500};

    for (int status : statuses) {
      zendeskApiMock.resetAll();
      stubTokenResponse(aResponse().withStatus(status).withBody("{\"error\":\"invalid_client\"}"));

      assertThatThrownBy(() -> minter().mint())
          .as("HTTP %d", status)
          .isInstanceOf(ZendeskOAuthException.class)
          .hasMessageContaining(String.valueOf(status));
    }
  }

  @Test
  public void mintMissingAccessTokenExpectException() {
    String[] bodies = {
      "{}",
      "{\"expires_in\":1800}",
      "{\"access_token\":null}",
      "{\"access_token\":\"\"}",
      "{\"access_token\":\"   \"}"
    };

    for (String body : bodies) {
      zendeskApiMock.resetAll();
      stubTokenResponse(ok(body));

      assertThatThrownBy(() -> minter().mint())
          .as("body %s", body)
          .isInstanceOf(ZendeskOAuthException.class)
          .hasMessageContaining("no usable access_token");
    }
  }

  @Test
  public void mintNonIntegerExpiresInExpectException() {
    // A stringified "900" must not fall back to the requested 1800, which would outlive what the
    // server granted.
    String[] malformed = {"\"900\"", "\"soon\"", "1800.75", "true", "{}", "[]", "\"\""};

    for (String expiresIn : malformed) {
      zendeskApiMock.resetAll();
      stubTokenResponse(
          ok("{\"access_token\":\"" + ACCESS_TOKEN + "\",\"expires_in\":" + expiresIn + "}"));

      assertThatThrownBy(() -> minter().mint())
          .as("expires_in %s", expiresIn)
          .isInstanceOf(ZendeskOAuthException.class)
          .hasMessageContaining("non-integer");
    }
  }

  @Test
  public void mintOutOfRangeExpiresInExpectException() {
    String[] outOfRange = {
      "0", "-1", "-1800", "172801", "9223372036854775807", "99999999999999999999999"
    };

    for (String expiresIn : outOfRange) {
      zendeskApiMock.resetAll();
      stubTokenResponse(
          ok("{\"access_token\":\"" + ACCESS_TOKEN + "\",\"expires_in\":" + expiresIn + "}"));

      assertThatThrownBy(() -> minter().mint())
          .as("expires_in %s", expiresIn)
          .isInstanceOf(ZendeskOAuthException.class)
          .hasMessageContaining("out-of-range");
    }
  }

  @Test
  public void mintAcceptsGrantedLifetimeAtTheUpperBound() {
    stubTokenResponse(ok(tokenBody(ACCESS_TOKEN, 172_800)));

    var token = minter().mint();

    assertThat(token.expiresAt()).isEqualTo(token.issuedAt().plusSeconds(172_800));
  }

  @Test
  public void mintEmptyBodyExpectException() {
    String[] empty = {"", "   "};

    for (String body : empty) {
      zendeskApiMock.resetAll();
      stubTokenResponse(ok(body));

      assertThatThrownBy(() -> minter().mint())
          .as("body '%s'", body)
          .isInstanceOf(ZendeskOAuthException.class)
          .hasMessageContaining("empty");
    }
  }

  @Test
  public void mintNonObjectBodyExpectException() {
    String[] bodies = {"null", "[]", "[{\"access_token\":\"x\"}]", "42", "\"str\"", "true"};

    for (String body : bodies) {
      zendeskApiMock.resetAll();
      stubTokenResponse(ok(body));

      assertThatThrownBy(() -> minter().mint())
          .as("body %s", body)
          .isInstanceOf(ZendeskOAuthException.class)
          .hasMessageContaining("not a JSON object");
    }
  }

  @Test
  public void mintMalformedBodyExpectException() {
    stubTokenResponse(ok("not json at all"));

    assertThatThrownBy(() -> minter().mint()).isInstanceOf(ZendeskOAuthException.class);
  }

  @Test
  public void mintExceptionOmitsClientSecret() {
    stubTokenResponse(aResponse().withStatus(401).withBody("{\"error\":\"invalid_client\"}"));

    assertThatThrownBy(() -> minter().mint())
        .isInstanceOf(ZendeskOAuthException.class)
        .hasMessageNotContaining(CLIENT_SECRET);
  }

  private void stubTokenResponse(
      com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder response) {
    zendeskApiMock.stubFor(post(urlPathEqualTo("/oauth/tokens")).willReturn(response));
  }

  private static String tokenBody(String accessToken, int expiresIn) {
    return "{\"access_token\":\"" + accessToken + "\",\"expires_in\":" + expiresIn + "}";
  }

  private HttpTokenMinter minter() {
    return minter(LIFETIME_SECONDS);
  }

  private HttpTokenMinter minter(int requestedLifetimeSeconds) {
    return minter(requestedLifetimeSeconds, Clock.systemUTC());
  }

  private HttpTokenMinter minter(Clock clock) {
    return minter(LIFETIME_SECONDS, clock);
  }

  private HttpTokenMinter minter(int requestedLifetimeSeconds, Clock clock) {
    return new HttpTokenMinter(
        httpClient, baseHostUrl, CLIENT_ID, CLIENT_SECRET, SCOPE, requestedLifetimeSeconds, clock);
  }
}
