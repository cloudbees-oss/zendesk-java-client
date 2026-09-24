package org.zendesk.client.v2;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.apache.commons.text.RandomStringGenerator;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

public class ClientCredentialsTokenProviderTest {

  private static final RandomStringGenerator RANDOM_STRING_GENERATOR =
      new RandomStringGenerator.Builder().withinRange('a', 'z').build();
  private static final String CLIENT_ID = RANDOM_STRING_GENERATOR.generate(12);
  private static final String CLIENT_SECRET = RANDOM_STRING_GENERATOR.generate(24);
  private static final String SCOPE = "read impersonate";
  private static final String ACCESS_TOKEN = RANDOM_STRING_GENERATOR.generate(30);

  @ClassRule
  public static WireMockClassRule zendeskApiClass =
      new WireMockClassRule(options().dynamicPort().dynamicHttpsPort());

  @Rule public WireMockClassRule zendeskApiMock = zendeskApiClass;

  private String hostname;

  @Before
  public void setUp() {
    hostname = String.format("http://localhost:%d", zendeskApiMock.port());
  }

  @Test
  public void buildPerformsNoNetworkIo() {
    stubToken();

    try (var provider = providerBuilder().build()) {
      assertThat(provider).isNotNull();
    }

    zendeskApiMock.verify(0, postRequestedFor(urlPathEqualTo("/oauth/tokens")));
  }

  @Test
  public void provideBearerTokenMintsOnceAndCaches() {
    stubToken();

    try (var provider = providerBuilder().setTokenLifetimeSeconds(600).build()) {
      assertThat(provider.provideBearerToken()).isEqualTo(ACCESS_TOKEN);
      assertThat(provider.provideBearerToken()).isEqualTo(ACCESS_TOKEN);
    }

    zendeskApiMock.verify(
        1,
        postRequestedFor(urlPathEqualTo("/oauth/tokens"))
            .withRequestBody(matchingJsonPath("$.grant_type", equalTo("client_credentials")))
            .withRequestBody(matchingJsonPath("$.client_id", equalTo(CLIENT_ID)))
            .withRequestBody(matchingJsonPath("$.client_secret", equalTo(CLIENT_SECRET)))
            .withRequestBody(matchingJsonPath("$.scope", equalTo(SCOPE)))
            .withRequestBody(matchingJsonPath("$.expires_in", equalTo("600"))));
  }

  @Test
  public void trailingSlashUrlMintsAtHostRoot() {
    stubToken();

    try (var provider =
        ClientCredentialsTokenProvider.builder(hostname + "/")
            .setClientCredentials(CLIENT_ID, CLIENT_SECRET, SCOPE)
            .build()) {
      provider.provideBearerToken();
    }

    zendeskApiMock.verify(1, postRequestedFor(urlPathEqualTo("/oauth/tokens")));
  }

  @Test
  public void mintFailureExpectException() {
    zendeskApiMock.stubFor(
        post(urlPathEqualTo("/oauth/tokens"))
            .willReturn(aResponse().withStatus(401).withBody("{\"error\":\"invalid_client\"}")));

    try (var provider = providerBuilder().build()) {
      assertThatThrownBy(provider::provideBearerToken)
          .isInstanceOf(ZendeskOAuthException.class)
          .hasMessageNotContaining(CLIENT_SECRET);
    }
  }

  @Test
  public void closedProviderExpectException() {
    stubToken();
    var provider = providerBuilder().build();
    provider.provideBearerToken();

    provider.close();

    assertThatThrownBy(provider::provideBearerToken)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("ClientCredentialsTokenProvider is closed");
  }

  @Test
  public void closeKeepsSuppliedClientOpen() throws Exception {
    try (var httpClient = new DefaultAsyncHttpClient()) {
      providerBuilder().setClient(httpClient).build().close();

      assertThat(httpClient.isClosed()).isFalse();
    }
  }

  @Test
  public void missingCredentialsExpectException() {
    var builder = ClientCredentialsTokenProvider.builder(hostname);

    assertThatThrownBy(builder::build)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("OAuth client id cannot be null");
  }

  @Test
  public void nullUrlExpectException() {
    var builder =
        ClientCredentialsTokenProvider.builder(null)
            .setClientCredentials(CLIENT_ID, CLIENT_SECRET, SCOPE);

    assertThatThrownBy(builder::build)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Zendesk URL cannot be null");
  }

  @Test
  public void blankScopeExpectException() {
    var builder =
        ClientCredentialsTokenProvider.builder(hostname)
            .setClientCredentials(CLIENT_ID, CLIENT_SECRET, " ");

    assertThatThrownBy(builder::build)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("OAuth scope cannot be blank");
  }

  @Test
  public void invalidLifetimeExpectException() {
    int[] invalid = {300, 172_800};

    for (int lifetime : invalid) {
      assertThatThrownBy(() -> providerBuilder().setTokenLifetimeSeconds(lifetime).build())
          .as("lifetime %d", lifetime)
          .isInstanceOf(IllegalArgumentException.class);
    }
  }

  @Test
  public void invalidRefreshThresholdExpectException() {
    double[] invalid = {0.0, 1.0, Double.NaN};

    for (double threshold : invalid) {
      assertThatThrownBy(() -> providerBuilder().setRefreshThreshold(threshold).build())
          .as("threshold %s", threshold)
          .isInstanceOf(IllegalArgumentException.class);
    }
  }

  private ClientCredentialsTokenProvider.Builder providerBuilder() {
    return ClientCredentialsTokenProvider.builder(hostname)
        .setClientCredentials(CLIENT_ID, CLIENT_SECRET, SCOPE);
  }

  private void stubToken() {
    zendeskApiMock.stubFor(
        post(urlPathEqualTo("/oauth/tokens"))
            .willReturn(ok("{\"access_token\":\"" + ACCESS_TOKEN + "\",\"expires_in\":1800}")));
  }
}
