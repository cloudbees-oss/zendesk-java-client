package org.zendesk.client.v2;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.tomakehurst.wiremock.client.BasicCredentials;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

public class ZendeskOAuthClientCredentialsTest {

  private static final RandomStringGenerator RANDOM_STRING_GENERATOR =
      new RandomStringGenerator.Builder().withinRange('a', 'z').build();
  private static final String CLIENT_ID = RANDOM_STRING_GENERATOR.generate(12);
  private static final String CLIENT_SECRET = RANDOM_STRING_GENERATOR.generate(24);
  private static final String SCOPE = "tickets:read";
  private static final String ACCESS_TOKEN = RANDOM_STRING_GENERATOR.generate(30);
  private static final String STATIC_TOKEN = RANDOM_STRING_GENERATOR.generate(15);
  private static final String USERNAME = RANDOM_STRING_GENERATOR.generate(10) + "@cloudbees.com";

  @ClassRule
  public static WireMockClassRule zendeskApiClass =
      new WireMockClassRule(options().dynamicPort().dynamicHttpsPort());

  @Rule public WireMockClassRule zendeskApiMock = zendeskApiClass;

  private String hostname;
  private Zendesk client;

  @Before
  public void setUp() {
    hostname = String.format("http://localhost:%d", zendeskApiMock.port());
  }

  @After
  public void closeClient() {
    if (client != null) {
      client.close();
      client = null;
    }
  }

  @Test
  public void buildPerformsNoNetworkIo() {
    stubToken();

    client = oauthBuilder().build();

    zendeskApiMock.verify(0, postRequestedFor(urlPathEqualTo("/oauth/tokens")));
  }

  @Test
  public void apiCallSendsMintedBearerToken() {
    stubToken();
    stubTicketCount();
    client = oauthBuilder().build();

    client.getTicketsCount();

    zendeskApiMock.verify(1, postRequestedFor(urlPathEqualTo("/oauth/tokens")));
    zendeskApiMock.verify(
        getRequestedFor(urlPathEqualTo("/api/v2/tickets/count.json"))
            .withHeader("Authorization", equalTo("Bearer " + ACCESS_TOKEN)));
  }

  @Test
  public void apiCallReusesMintedToken() {
    stubToken();
    stubTicketCount();
    client = oauthBuilder().build();

    client.getTicketsCount();
    client.getTicketsCount();
    client.getTicketsCount();

    zendeskApiMock.verify(1, postRequestedFor(urlPathEqualTo("/oauth/tokens")));
  }

  @Test
  public void tokenRequestIgnoresObjectMapperCustomizer() {
    stubToken();
    // Uppercases every serialized String. If the credential path shared the client's customized
    // mapper, the client secret would be corrupted on the wire; its own mapper keeps it verbatim.
    Function<ObjectMapper, ObjectMapper> uppercaseStrings =
        mapper -> {
          SimpleModule module = new SimpleModule();
          module.addSerializer(
              String.class,
              new JsonSerializer<String>() {
                @Override
                public void serialize(
                    String value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                  gen.writeString(value.toUpperCase(Locale.ROOT));
                }
              });
          return mapper.registerModule(module);
        };
    client = oauthBuilder().customizeObjectMapper(uppercaseStrings).build();

    client.warmUp();

    zendeskApiMock.verify(
        postRequestedFor(urlPathEqualTo("/oauth/tokens"))
            .withRequestBody(matchingJsonPath("$.client_secret", equalTo(CLIENT_SECRET)))
            .withRequestBody(matchingJsonPath("$.grant_type", equalTo("client_credentials"))));
  }

  @Test
  public void warmUpMintsUpFront() {
    stubToken();
    stubTicketCount();
    client = oauthBuilder().build();

    client.warmUp();

    zendeskApiMock.verify(1, postRequestedFor(urlPathEqualTo("/oauth/tokens")));

    client.getTicketsCount();

    // No second call to mint
    zendeskApiMock.verify(1, postRequestedFor(urlPathEqualTo("/oauth/tokens")));
  }

  @Test
  public void warmUpFailureExpectException() {
    zendeskApiMock.stubFor(
        post(urlPathEqualTo("/oauth/tokens"))
            .willReturn(aResponse().withStatus(401).withBody("{\"error\":\"invalid_client\"}")));
    client = oauthBuilder().build();

    assertThatThrownBy(client::warmUp).isInstanceOf(ZendeskOAuthException.class);
  }

  @Test
  public void warmUpIsNoOpForStaticCredentials() {
    stubToken();

    for (Consumer<Zendesk.Builder> credentials : staticCredentialSetters()) {
      var builder = new Zendesk.Builder(hostname);
      credentials.accept(builder);
      try (var staticClient = builder.build()) {
        staticClient.warmUp();
      }
    }

    zendeskApiMock.verify(0, postRequestedFor(urlPathEqualTo("/oauth/tokens")));
  }

  @Test
  public void setOauthClientCredentialsClearsStaticCredentials() {
    stubToken();
    stubTicketCount();
    client =
        new Zendesk.Builder(hostname)
            .setUsername(USERNAME)
            .setToken(STATIC_TOKEN)
            .setOauthToken(STATIC_TOKEN)
            .setOauthClientCredentials(CLIENT_ID, CLIENT_SECRET, SCOPE)
            .build();

    client.getTicketsCount();

    zendeskApiMock.verify(
        getRequestedFor(urlPathEqualTo("/api/v2/tickets/count.json"))
            .withHeader("Authorization", equalTo("Bearer " + ACCESS_TOKEN)));
  }

  @Test
  public void staticCredentialSettersClearClientCredentials() {
    stubToken();
    stubTicketCount();

    for (Consumer<Zendesk.Builder> override : staticCredentialSetters()) {
      var builder =
          new Zendesk.Builder(hostname).setOauthClientCredentials(CLIENT_ID, CLIENT_SECRET, SCOPE);
      override.accept(builder);
      try (var overridden = builder.build()) {
        overridden.getTicketsCount();
      }
    }

    // Whichever static credential won, no client-credentials mint should ever have happened.
    zendeskApiMock.verify(0, postRequestedFor(urlPathEqualTo("/oauth/tokens")));
  }

  @Test
  public void nullOauthClientCredentialsExpectException() {
    String[][] invalidCredentials = {
      {null, CLIENT_SECRET, SCOPE, "client id", "OAuth client id cannot be null"},
      {CLIENT_ID, null, SCOPE, "client secret", "OAuth client secret cannot be null"},
      {CLIENT_ID, CLIENT_SECRET, null, "scope", "OAuth scope cannot be null"}
    };

    for (String[] testCase : invalidCredentials) {
      var builder =
          new Zendesk.Builder(hostname)
              .setOauthClientCredentials(testCase[0], testCase[1], testCase[2]);

      assertThatThrownBy(builder::build)
          .as("null OAuth %s", testCase[3])
          .isInstanceOf(NullPointerException.class)
          .hasMessage(testCase[4]);
    }
  }

  @Test
  public void nullOauthClientIdWithPriorCredentialsExpectException() {
    var builder =
        new Zendesk.Builder(hostname)
            .setUsername(USERNAME)
            .setPassword(STATIC_TOKEN)
            .setOauthClientCredentials(null, CLIENT_SECRET, SCOPE);

    assertThatThrownBy(builder::build)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("OAuth client id cannot be null");
  }

  @Test
  public void blankOauthClientCredentialsExpectException() {
    String[][] invalidCredentials = {
      {"", CLIENT_SECRET, SCOPE, "client id"},
      {" \t", CLIENT_SECRET, SCOPE, "client id"},
      {CLIENT_ID, "", SCOPE, "client secret"},
      {CLIENT_ID, "\t\r\n", SCOPE, "client secret"},
      {CLIENT_ID, CLIENT_SECRET, "", "scope"},
      {CLIENT_ID, CLIENT_SECRET, " \t\r\n", "scope"}
    };

    for (String[] testCase : invalidCredentials) {
      var builder =
          new Zendesk.Builder(hostname)
              .setOauthClientCredentials(testCase[0], testCase[1], testCase[2]);

      assertThatThrownBy(builder::build)
          .as("blank OAuth %s", testCase[3])
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("OAuth " + testCase[3] + " cannot be blank");
    }
  }

  @Test
  public void existingAuthPathsUnaffected() {
    stubTicketCount();

    try (var tokenClient =
        new Zendesk.Builder(hostname).setUsername(USERNAME).setToken(STATIC_TOKEN).build()) {
      tokenClient.getTicketsCount();
    }
    zendeskApiMock.verify(
        getRequestedFor(urlPathEqualTo("/api/v2/tickets/count.json"))
            .withBasicAuth(new BasicCredentials(USERNAME + "/token", STATIC_TOKEN)));

    WireMock.reset();
    stubTicketCount();

    try (var bearerClient = new Zendesk.Builder(hostname).setOauthToken(STATIC_TOKEN).build()) {
      bearerClient.getTicketsCount();
    }
    zendeskApiMock.verify(
        getRequestedFor(urlPathEqualTo("/api/v2/tickets/count.json"))
            .withHeader("Authorization", equalTo("Bearer " + STATIC_TOKEN)));
    zendeskApiMock.verify(0, postRequestedFor(urlPathEqualTo("/oauth/tokens")));
  }

  @Test
  public void anonymousClientStillSendsBearerNull() {
    stubTicketCount();

    try (var anonymous = new Zendesk.Builder(hostname).build()) {
      anonymous.getTicketsCount();
    }

    // Pre-existing behavior: the new branch must not change what an anonymous client sends.
    zendeskApiMock.verify(
        getRequestedFor(urlPathEqualTo("/api/v2/tickets/count.json"))
            .withHeader("Authorization", equalTo("Bearer null")));
  }

  @Test
  public void invalidLifetimeExpectException() {
    int[] invalid = {0, -1, 300, 299, 172_800, 200_000};

    for (int lifetime : invalid) {
      assertThatThrownBy(() -> oauthBuilder().setOauthTokenLifetimeSeconds(lifetime).build())
          .as("lifetime %d", lifetime)
          .isInstanceOf(IllegalArgumentException.class);
    }
  }

  @Test
  public void validLifetimeBoundaries() {
    int[] valid = {301, 1800, 172_799};

    for (int lifetime : valid) {
      try (var built = oauthBuilder().setOauthTokenLifetimeSeconds(lifetime).build()) {
        assertThat(built).as("lifetime %d", lifetime).isNotNull();
      }
    }
  }

  @Test
  public void invalidRefreshThresholdExpectException() {
    double[] invalid = {
      0.0,
      1.0,
      -0.1,
      1.5,
      Double.NaN,
      Double.POSITIVE_INFINITY,
      Double.NEGATIVE_INFINITY,
      -Double.MIN_VALUE
    };

    for (double threshold : invalid) {
      assertThatThrownBy(() -> oauthBuilder().setOauthRefreshThreshold(threshold).build())
          .as("threshold %s", threshold)
          .isInstanceOf(IllegalArgumentException.class);
    }
  }

  @Test
  public void lifetimeValidationOnlyAppliesToClientCredentials() {
    try (var tokenClient =
        new Zendesk.Builder(hostname)
            .setUsername(USERNAME)
            .setToken(STATIC_TOKEN)
            .setOauthTokenLifetimeSeconds(-1)
            .build()) {
      assertThat(tokenClient).isNotNull();
    }
  }

  /** Every pre-existing way to supply credentials, each of which excludes client credentials. */
  private static List<Consumer<Zendesk.Builder>> staticCredentialSetters() {
    return Arrays.asList(
        builder -> builder.setOauthToken(STATIC_TOKEN),
        builder -> builder.setUsername(USERNAME).setToken(STATIC_TOKEN),
        builder -> builder.setUsername(USERNAME).setPassword(STATIC_TOKEN));
  }

  private Zendesk.Builder oauthBuilder() {
    return new Zendesk.Builder(hostname).setOauthClientCredentials(CLIENT_ID, CLIENT_SECRET, SCOPE);
  }

  private void stubToken() {
    zendeskApiMock.stubFor(
        post(urlPathEqualTo("/oauth/tokens"))
            .willReturn(ok("{\"access_token\":\"" + ACCESS_TOKEN + "\",\"expires_in\":1800}")));
  }

  private void stubTicketCount() {
    zendeskApiMock.stubFor(
        WireMock.get(urlPathEqualTo("/api/v2/tickets/count.json"))
            .willReturn(
                ok("{\"count\":{\"value\":42,\"refreshed_at\":\"2026-01-01T00:00:00Z\"}}")));
  }
}
