package org.zendesk.client.v2;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.absent;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import java.util.Collections;
import java.util.function.Function;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.zendesk.client.v2.model.Comment;
import org.zendesk.client.v2.model.Status;
import org.zendesk.client.v2.model.Ticket;

/**
 * Integration tests for ticket creation with idempotency key support.
 * Uses WireMock to simulate Zendesk API responses.
 */
public class TicketIdempotencyTest {

  private static final String CREATE_TICKET_PATH = "/api/v2/tickets.json";
  private static final long TICKET_ID = 12345L;
  private static final String TICKET_KEY = "test-key-123";

  @ClassRule
  public static WireMockClassRule zendeskApiClass =
      new WireMockClassRule(options().dynamicPort().dynamicHttpsPort());

  @Rule public WireMockClassRule zendeskApiMock = zendeskApiClass;

  private Zendesk client;
  private final ObjectMapper objectMapper = Zendesk.createMapper(Function.identity());

  @Before
  public void setUp() {
    client = new Zendesk.Builder("http://localhost:" + zendeskApiMock.port())
        .setUsername("zana@example.com")
        .setToken("still-sane-exile")
        .build();
  }

  @After
  public void tearDown() {
    client.close();
    client = null;
  }

  @Test
  public void createTicket_withoutIdempotencyKey_doesNotSendHeader() throws JsonProcessingException {
    Ticket requestTicket = createSampleTicket();
    requestTicket.setIdempotencyKey(null);

    Ticket responseTicket = createResponseTicket();
    String expectedJsonResponse =
        objectMapper.writeValueAsString(Collections.singletonMap("ticket", responseTicket));

    zendeskApiMock.stubFor(
        post(urlEqualTo(CREATE_TICKET_PATH))
            .willReturn(ok().withBody(expectedJsonResponse)));

    Ticket result = client.createTicket(requestTicket);

    verifyRequest(null);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(TICKET_ID);
    assertThat(result.getIdempotencyKey()).isNull();
    assertThat(result.getIsIdempotencyHit()).isNull();
  }

  @Test
  public void createTicket_withIdempotencyKeyFirstRequest_sendsMissHeader()
      throws JsonProcessingException {
    Ticket requestTicket = createSampleTicket();
    requestTicket.setIdempotencyKey(TICKET_KEY);

    Ticket responseTicket = createResponseTicket();
    String expectedJsonResponse =
        objectMapper.writeValueAsString(Collections.singletonMap("ticket", responseTicket));

    zendeskApiMock.stubFor(
        post(urlEqualTo(CREATE_TICKET_PATH))
            .withHeader(IdempotencyUtil.IDEMPOTENCY_KEY_HEADER, equalTo(TICKET_KEY))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader(
                        IdempotencyUtil.IDEMPOTENCY_LOOKUP_HEADER,
                        IdempotencyUtil.IDEMPOTENCY_LOOKUP_MISS)
                    .withBody(expectedJsonResponse)));

    Ticket result = client.createTicket(requestTicket);

    verifyRequest(TICKET_KEY);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(TICKET_ID);
    assertThat(result.getIdempotencyKey()).isEqualTo(TICKET_KEY);
    assertThat(result.getIsIdempotencyHit()).isFalse();
  }

  @Test
  public void createTicket_withIdempotencyKeyDuplicateRequest_sendsHitHeader()
      throws JsonProcessingException {
    Ticket requestTicket = createSampleTicket();
    requestTicket.setIdempotencyKey(TICKET_KEY);

    Ticket responseTicket = createResponseTicket();
    String expectedJsonResponse =
        objectMapper.writeValueAsString(Collections.singletonMap("ticket", responseTicket));

    zendeskApiMock.stubFor(
        post(urlEqualTo(CREATE_TICKET_PATH))
            .withHeader(IdempotencyUtil.IDEMPOTENCY_KEY_HEADER, equalTo(TICKET_KEY))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withHeader(
                        IdempotencyUtil.IDEMPOTENCY_LOOKUP_HEADER,
                        IdempotencyUtil.IDEMPOTENCY_LOOKUP_HIT)
                    .withBody(expectedJsonResponse)));

    Ticket result = client.createTicket(requestTicket);

    verifyRequest(TICKET_KEY);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(TICKET_ID);
    assertThat(result.getIdempotencyKey()).isEqualTo(TICKET_KEY);
    assertThat(result.getIsIdempotencyHit()).isTrue();
  }

  @Test
  public void createTicket_withIdempotencyKeyNoHeader_doesNotSetIdempotencyFields()
      throws JsonProcessingException {
    Ticket requestTicket = createSampleTicket();
    requestTicket.setIdempotencyKey(TICKET_KEY);

    Ticket responseTicket = createResponseTicket();
    String expectedJsonResponse =
        objectMapper.writeValueAsString(Collections.singletonMap("ticket", responseTicket));

    zendeskApiMock.stubFor(
        post(urlEqualTo(CREATE_TICKET_PATH))
            .withHeader(IdempotencyUtil.IDEMPOTENCY_KEY_HEADER, equalTo(TICKET_KEY))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody(expectedJsonResponse)));

    Ticket result = client.createTicket(requestTicket);

    zendeskApiMock.verify(
        postRequestedFor(urlEqualTo(CREATE_TICKET_PATH))
            .withHeader(IdempotencyUtil.IDEMPOTENCY_KEY_HEADER, equalTo(TICKET_KEY)));

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(TICKET_ID);
    // Idempotency fields should not be set if server doesn't return the header
    assertThat(result.getIdempotencyKey()).isNull();
    assertThat(result.getIsIdempotencyHit()).isNull();
  }

  private Ticket createSampleTicket() {
    Ticket ticket = new Ticket();
    ticket.setSubject("Test Ticket");
    ticket.setComment(new Comment("This is a test ticket"));
    ticket.setRequesterId(123456L);
    return ticket;
  }

  private Ticket createResponseTicket() {
    Ticket ticket = createSampleTicket();
    ticket.setId(TICKET_ID);
    ticket.setStatus(Status.OPEN);
    return ticket;
  }

  private void verifyRequest(String idempotencyKey) {
    StringValuePattern pattern = idempotencyKey == null
        ? absent()
        : equalTo(idempotencyKey);
    zendeskApiMock.verify(
        postRequestedFor(urlEqualTo(CREATE_TICKET_PATH))
            .withHeader(IdempotencyUtil.IDEMPOTENCY_KEY_HEADER, pattern));
  }
}
