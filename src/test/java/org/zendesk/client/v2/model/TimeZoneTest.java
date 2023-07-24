package org.zendesk.client.v2.model;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.*;
import org.zendesk.client.v2.Zendesk;

public class TimeZoneTest {

  private static final String MOCK_URL_FORMATTED_STRING = "http://localhost:%d";
  public static final RandomStringGenerator RANDOM_STRING_GENERATOR =
      new RandomStringGenerator.Builder().withinRange('a', 'z').build();
  private static final String MOCK_API_TOKEN = RANDOM_STRING_GENERATOR.generate(15);
  private static final String MOCK_USERNAME =
      RANDOM_STRING_GENERATOR.generate(10).toLowerCase() + "@cloudbees.com";

  @ClassRule
  public static WireMockClassRule zendeskApiClass =
      new WireMockClassRule(
          options().dynamicPort().dynamicHttpsPort().usingFilesUnderClasspath("wiremock"));

  @Rule public WireMockClassRule zendeskApiMock = zendeskApiClass;

  private Zendesk client;
  private final ObjectMapper objectMapper = Zendesk.createMapper();

  @Before
  public void setUp() {
    int ephemeralPort = zendeskApiMock.port();
    String hostname = String.format(MOCK_URL_FORMATTED_STRING, ephemeralPort);
    client =
        new Zendesk.Builder(hostname).setUsername(MOCK_USERNAME).setToken(MOCK_API_TOKEN).build();
  }

  @After
  public void closeClient() {
    if (client != null) {
      client.close();
    }
    client = null;
  }

  @Test
  public void getTimeZones() throws JsonProcessingException {

    TimeZone timeZone1 = new TimeZone();
    timeZone1.setTranslatedName("Pacific Time (US & Canada)");
    timeZone1.setName("Pacific Time (US & Canada)");
    timeZone1.setIanaName("America/Los_Angeles");
    timeZone1.setOffset(-420);
    timeZone1.setFormattedOffset("GMT-07:00");

    TimeZone timeZone2 = new TimeZone();
    timeZone1.setTranslatedName("Kyiv");
    timeZone1.setName("Kyiv");
    timeZone1.setIanaName("Europe/Kiev");
    timeZone1.setOffset(180);
    timeZone1.setFormattedOffset("GMT+03:00");

    String expectedJsonResponse =
        objectMapper.writeValueAsString(
            Collections.singletonMap("time_zones", Arrays.asList(timeZone1, timeZone2)));

    zendeskApiMock.stubFor(
        get(urlPathEqualTo("/api/v2/time_zones.json"))
            .willReturn(ok().withBody(expectedJsonResponse)));

    List<TimeZone> timeZones = client.getTimeZones();

    zendeskApiMock.verify(getRequestedFor(urlPathEqualTo("/api/v2/time_zones.json")));

    assertThat(timeZones).containsExactly(timeZone1, timeZone2);
  }
}
