package org.zendesk.client.v2;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.zendesk.client.v2.model.hc.ContentTag;

public class ContentTagsTest {

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

  @Before
  public void setUp() throws Exception {
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
  public void getContentTags_willPageOverMultiplePages() throws Exception {
    zendeskApiMock.stubFor(
        get(urlPathEqualTo("/api/v2/guide/content_tags"))
            .withQueryParam("page%5Bsize%5D", equalTo("2"))
            .willReturn(ok().withBodyFile("content_tags/content_tag_search_first_page.json")));
    zendeskApiMock.stubFor(
        get(urlPathEqualTo("/api/v2/guide/content_tags"))
            .withQueryParam("page%5Bsize%5D", equalTo("2"))
            .withQueryParam("page%5Bafter%5D", equalTo("first_after_cursor"))
            .willReturn(ok().withBodyFile("content_tags/content_tag_search_second_page.json")));
    zendeskApiMock.stubFor(
        get(urlPathEqualTo("/api/v2/guide/content_tags"))
            .withQueryParam("page%5Bsize%5D", equalTo("2"))
            .withQueryParam("page%5Bafter%5D", equalTo("second_after_cursor"))
            .willReturn(ok().withBodyFile("content_tags/content_tag_search_third_page.json")));

    Iterable<ContentTag> actualResults = client.getContentTags(2);

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    df.setTimeZone(TimeZone.getTimeZone("UTC"));

    assertThat(actualResults)
        .containsExactly(
            new ContentTag(
                "11111111111111111111111111",
                "first name",
                df.parse("2023-03-13 10:01:00"),
                df.parse("2023-03-13 10:01:01")),
            new ContentTag(
                "22222222222222222222222222",
                "second name",
                df.parse("2023-03-13 10:02:00"),
                df.parse("2023-03-13 10:02:02")),
            new ContentTag(
                "33333333333333333333333333",
                "third name",
                df.parse("2023-03-13 10:03:00"),
                df.parse("2023-03-13 10:03:03")),
            new ContentTag(
                "44444444444444444444444444",
                "fourth name",
                df.parse("2023-03-13 10:04:00"),
                df.parse("2023-03-13 10:04:04")),
            new ContentTag(
                "55555555555555555555555555",
                "fifth name",
                df.parse("2023-03-13 10:05:00"),
                df.parse("2023-03-13 10:05:05")));
  }
}
