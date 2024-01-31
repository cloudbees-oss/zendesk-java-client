package org.zendesk.client.v2.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.zendesk.client.v2.Zendesk;

public class LocaleTest {
  @Test
  public void testLocaleDeserialization() throws Exception {
    String json =
        "{"
            + "\"url\": \"https://acme.zendesk.com/api/v2/locales/en-US.json\","
            + "\"id\": 1,"
            + "\"locale\": \"en-US\","
            + "\"name\": \"English\","
            + "\"created_at\": \"2023-08-13T19:23:16Z\","
            + "\"updated_at\": \"2023-09-21T19:23:16Z\""
            + "}";

    Locale locale = Zendesk.createMapper().readValue(json, Locale.class);

    assertThat(locale.getUrl(), is("https://acme.zendesk.com/api/v2/locales/en-US.json"));
    assertThat(locale.getId(), is(1L));
    assertThat(locale.getLocale(), is("en-US"));
    assertThat(locale.getName(), is("English"));
    assertThat(locale.getCreatedAt().getTime(), is(1691954596000L));
    assertThat(locale.getUpdatedAt().getTime(), is(1695324196000L));
  }
}
