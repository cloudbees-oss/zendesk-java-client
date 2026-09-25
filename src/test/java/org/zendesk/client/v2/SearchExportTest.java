package org.zendesk.client.v2;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.*;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;

public class SearchExportTest {
  @Rule public WireMockRule api = new WireMockRule(options().dynamicPort());

  @Test
  public void exportUsesFilterTypeAndConsumesEveryCursorPage() {
    String base = "http://localhost:" + api.port();
    api.stubFor(
        get(urlPathEqualTo("/api/v2/search/export"))
            .withQueryParam("query", equalTo("status:solved tags:example"))
            // WireMock 2.27 retains percent encoding in bracketed parameter names.
            .withQueryParam("filter%5Btype%5D", equalTo("ticket"))
            .withQueryParam("page%5Bsize%5D", equalTo("50"))
            .willReturn(
                okJson(
                    "{\"results\":[{\"id\":1}],\"links\":{\"next\":\""
                        + base
                        + "/api/v2/search/export?page%5Bafter%5D=next\"},\"meta\":{\"has_more\":true}}")));
    api.stubFor(
        get(urlPathEqualTo("/api/v2/search/export"))
            .withQueryParam("page%5Bafter%5D", equalTo("next"))
            .willReturn(
                okJson(
                    "{\"results\":[{\"id\":2}],\"links\":{\"next\":null},\"meta\":{\"has_more\":false}}")));
    try (Zendesk zd = new Zendesk.Builder(base).setOauthToken("test-token").build()) {
      List<Long> ids = new ArrayList<>();
      zd.getTicketFromSearchWithExport("status:solved tags:example", 50)
          .forEach(ticket -> ids.add(ticket.getId()));
      assertEquals(Arrays.asList(1L, 2L), ids);
    }
    api.verify(2, getRequestedFor(urlPathEqualTo("/api/v2/search/export")));
  }
}
