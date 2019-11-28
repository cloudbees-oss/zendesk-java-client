package org.zendesk.client.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.zendesk.client.v2.model.SearchResults;
import org.zendesk.client.v2.model.SortOrder;
import org.zendesk.client.v2.model.Ticket;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * An initial attempt at a unit test that uses wiremock to test the client without requiring a running zendesk client
 * @author rbolles on 2/8/18.
 */
public class SearchTest {

    private static final String MOCK_URL_FORMATTED_STRING = "http://localhost:%d";
    public static final RandomStringGenerator RANDOM_STRING_GENERATOR =
            new RandomStringGenerator.Builder().withinRange('a', 'z').build();
    private static final String MOCK_API_TOKEN = RANDOM_STRING_GENERATOR.generate(15);
    private static final String MOCK_USERNAME = RANDOM_STRING_GENERATOR.generate(10).toLowerCase() + "@cloudbees.com";

    @ClassRule
    public static WireMockClassRule zendeskApiClass = new WireMockClassRule(options()
            .dynamicPort()
            .dynamicHttpsPort()
    );

    @Rule
    public WireMockClassRule zendeskApiMock = zendeskApiClass;

    private Zendesk client;
    //use a mapper that is identical to what the client will use
    private ObjectMapper objectMapper = Zendesk.createMapper();


    @Before
    public void setUp() throws Exception {
        int ephemeralPort = zendeskApiMock.port();

        String hostname = String.format(MOCK_URL_FORMATTED_STRING, ephemeralPort);

        client = new Zendesk.Builder(hostname)
                .setUsername(MOCK_USERNAME)
                .setToken(MOCK_API_TOKEN)
                .build();
    }

    @After
    public void closeClient() {
        if (client != null) {
            client.close();
        }
        client = null;
    }


    @Test
    public void getSearchResults_Sorting() throws JsonProcessingException, UnsupportedEncodingException {

        String query = "Greenbriar";
        String expectedQuery = query + " type:ticket";
        String expectedSortBy = "updated_at";
        String expectedSortOrder = "asc";

        Random r = new Random(System.currentTimeMillis());
        Ticket expectedTicket1 = new Ticket();
        expectedTicket1.setId(Math.abs(r.nextLong()));

        Ticket expectedTicket2 = new Ticket();
        expectedTicket2.setId(Math.abs(r.nextLong()));

        SearchResults<Ticket> searchResults = new SearchResults<>();
        searchResults.setResults(Arrays.asList(expectedTicket1, expectedTicket2));
        searchResults.setCount(2);


        zendeskApiMock.stubFor(
                get(
                        urlPathEqualTo("/api/v2/search.json"))
                        .withQueryParam("query", equalTo(expectedQuery))
                        .withQueryParam("sort_by", equalTo(expectedSortBy))
                        .withQueryParam("sort_order", equalTo(expectedSortOrder))
                        .willReturn(ok()
                                .withBody(objectMapper.writeValueAsString(searchResults))
                        )

        );

        Iterable<Ticket> actualResults = client.getSearchResults(Ticket.class, query, expectedSortBy, SortOrder.ASCENDING);

        assertThat(actualResults).as("actual results")
                .isNotNull()
                .hasSize(2)
                .extracting("id")
                .containsExactly(expectedTicket1.getId(), expectedTicket2.getId());



    }
}
