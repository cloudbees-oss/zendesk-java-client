package org.zendesk.client.v2;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.text.RandomStringGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.zendesk.client.v2.model.User;

/**
 * An initial attempt at a unit test that uses wiremock to test the client without requiring a running zendesk client
 * @author rbolles on 2/8/18.
 */
public class UserTest {

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
    public void mergeUsers() throws JsonProcessingException {

        long userThatWillBeMerged = 1234L;
        long userThatWillRemain = 2345L;


        User userObjectInRequest = new User();
        userObjectInRequest.setId(userThatWillRemain);
        String expectedJsonPayload = objectMapper.writeValueAsString(Collections.singletonMap("user", userObjectInRequest));

        User userObjectInResponse = new User();
        userObjectInResponse.setId(userThatWillRemain);
        userObjectInResponse.setPhone("867-5309"); //a field that wasn't in the request
        String expectedJsonResponse = objectMapper.writeValueAsString(Collections.singletonMap("user", userObjectInResponse));

        zendeskApiMock.stubFor(
                put(
                        urlPathEqualTo("/api/v2/users/1234/merge.json"))
                        .withRequestBody(equalToJson(expectedJsonPayload))
                        .willReturn(ok()
                                .withBody(expectedJsonResponse)
                        )
        );

        User remainingUser = client.mergeUsers(userThatWillRemain, userThatWillBeMerged);

        zendeskApiMock.verify(putRequestedFor(
            urlEqualTo("/api/v2/users/1234/merge.json"))
                   .withHeader("Content-Type", equalTo(APPLICATION_JSON+"; charset=UTF-8"))
                   .withRequestBody(equalToJson(expectedJsonPayload))
        );

        assertThat(remainingUser).as("result")
                .isNotNull()
                .isEqualToComparingFieldByField(userObjectInResponse);

    }

    @Test
    public void getUsersByExternalIds_canBeCalledWithEitherLongsOrStrings() throws JsonProcessingException {

        User user123 = new User();
        user123.setId(1L);
        user123.setExternalId("123");
        user123.setName("user123");
        User user456 = new User();
        user456.setId(2L);
        user456.setExternalId("456");
        user456.setName("user456");
        User user789 = new User();
        user789.setId(3L);
        user789.setExternalId("789");
        user789.setName("user789");

        String expectedJsonResponse = objectMapper.writeValueAsString(
                Collections.singletonMap("users", Arrays.asList(user123, user456, user789)));

        zendeskApiMock.stubFor(
                get(
                        urlPathEqualTo("/api/v2/users/show_many.json"))
                        .withQueryParam("external_ids", equalTo("123,456,789"))
                        .willReturn(ok()
                                .withBody(expectedJsonResponse)
                        )
        );

        List<User> usersByLongExternalIds = client.getUsersByExternalIds(123L, 456L, 789L);

        zendeskApiMock.verify(getRequestedFor(
                urlPathEqualTo("/api/v2/users/show_many.json"))
                .withQueryParam("external_ids", equalTo("123,456,789"))
        );

        assertThat(usersByLongExternalIds).containsExactly(user123, user456, user789);

        List<User> usersByStringExternalIds = client.getUsersByExternalIds("123", "456", "789");

        zendeskApiMock.verify(getRequestedFor(
                urlPathEqualTo("/api/v2/users/show_many.json"))
                .withQueryParam("external_ids", equalTo("123,456,789"))
        );

        assertThat(usersByStringExternalIds).containsExactly(user123, user456, user789);
    }
}
