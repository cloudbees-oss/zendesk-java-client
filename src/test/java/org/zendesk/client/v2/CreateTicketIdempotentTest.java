package org.zendesk.client.v2;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.asynchttpclient.ListenableFuture;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.zendesk.client.v2.model.Comment;
import org.zendesk.client.v2.model.IdempotentResult;
import org.zendesk.client.v2.model.Status;
import org.zendesk.client.v2.model.Ticket;

/**
 * Integration tests for ticket creation with idempotency key support. Uses WireMock to simulate
 * Zendesk API responses.
 */
public class CreateTicketIdempotentTest {

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
    client =
        new Zendesk.Builder("http://localhost:" + zendeskApiMock.port())
            .setUsername("zana@example.com")
            .setToken("still-sane-exile")
            .build();
  }

  @After
  public void tearDown() {
    verifyRequest();

    client.close();
    client = null;
  }

  @Test
  public void idempotencyLookupMiss() throws JsonProcessingException {
    stubPostTicket(createExpectedResponse(IdempotencyUtil.IDEMPOTENCY_LOOKUP_MISS));
    IdempotentResult<Ticket> result = client.createTicketIdempotent(createTicket(), TICKET_KEY);

    assertThat(result).isNotNull();
    assertThat(result.isDuplicateRequest()).isFalse();
    assertThat(result.get())
        .satisfies(
            new Consumer<>() {
              @Override
              public void accept(Ticket ticket) {
                assertThat(ticket).isNotNull();
                assertThat(ticket.getId()).isEqualTo(TICKET_ID);
              }
            });
  }

  @Test
  public void idempotencyLookupHit() throws JsonProcessingException {
    stubPostTicket(createExpectedResponse(IdempotencyUtil.IDEMPOTENCY_LOOKUP_HIT));
    IdempotentResult<Ticket> result = client.createTicketIdempotent(createTicket(), TICKET_KEY);

    assertThat(result).isNotNull();
    assertThat(result.isDuplicateRequest()).isTrue();
    assertThat(result.get())
        .satisfies(
            new Consumer<>() {
              @Override
              public void accept(Ticket ticket) {
                assertThat(ticket).isNotNull();
                assertThat(ticket.getId()).isEqualTo(TICKET_ID);
              }
            });
  }

  @Test
  public void idempotencyLookupInvalid() throws JsonProcessingException {
    stubPostTicket(createExpectedResponse("InvalidValue"));
    assertThatThrownBy(
            new ThrowingCallable() {
              @Override
              public void call() {
                client.createTicketIdempotent(createTicket(), TICKET_KEY);
              }
            })
        .isExactlyInstanceOf(ZendeskException.class);
  }

  @Test
  public void idempotencyLookupAbsent() throws JsonProcessingException {
    stubPostTicket(createExpectedResponse(null));

    assertThatThrownBy(
            new ThrowingCallable() {
              @Override
              public void call() {
                client.createTicketIdempotent(createTicket(), TICKET_KEY);
              }
            })
        .isExactlyInstanceOf(ZendeskException.class);
  }

  @Test
  public void idempotencyConflict() throws JsonProcessingException {
    stubPostTicket(
        aResponse()
            .withStatus(400)
            .withBody(
                objectMapper.writeValueAsString(
                    Collections.singletonMap("error", IdempotencyUtil.IDEMPOTENCY_ERROR_NAME))));

    assertThatThrownBy(
            new ThrowingCallable() {
              @Override
              public void call() {
                client.createTicketIdempotent(createTicket(), TICKET_KEY);
              }
            })
        .isExactlyInstanceOf(ZendeskResponseIdempotencyConflictException.class);
  }

  @Test
  public void errorWithEmptyResponseBody() {
    // White-box testing a known edge case where an older Jackson version would throw
    // a `NullPointerException`.
    stubPostTicket(aResponse().withStatus(400).withBody(""));

    ListenableFuture<IdempotentResult<Ticket>> future =
        client.createTicketIdempotentAsync(createTicket(), TICKET_KEY);
    assertThat(future.toCompletableFuture())
        .completesExceptionallyWithin(Duration.ofSeconds(5))
        .withThrowableOfType(ExecutionException.class)
        .havingCause()
        .isInstanceOf(ZendeskResponseException.class)
        .withNoCause();
  }

  private Ticket createTicket() {
    Ticket ticket = new Ticket();
    ticket.setSubject("Test Ticket");
    ticket.setComment(new Comment("This is a test ticket"));
    ticket.setRequesterId(123456L);
    return ticket;
  }

  private ResponseDefinitionBuilder createExpectedResponse(String idempotencyLookupValue)
      throws JsonProcessingException {
    Ticket ticket = createTicket();
    ticket.setId(TICKET_ID);
    ticket.setStatus(Status.OPEN);
    String ticketJson = objectMapper.writeValueAsString(Collections.singletonMap("ticket", ticket));

    ResponseDefinitionBuilder response = aResponse().withStatus(201).withBody(ticketJson);

    if (idempotencyLookupValue != null) {
      response =
          response.withHeader(IdempotencyUtil.IDEMPOTENCY_LOOKUP_HEADER, idempotencyLookupValue);
    }

    return response;
  }

  private void stubPostTicket(ResponseDefinitionBuilder response) {
    zendeskApiMock.stubFor(
        post(urlEqualTo(CREATE_TICKET_PATH))
            .withHeader(IdempotencyUtil.IDEMPOTENCY_KEY_HEADER, equalTo(TICKET_KEY))
            .willReturn(response));
  }

  private void verifyRequest() {
    zendeskApiMock.verify(
        postRequestedFor(urlEqualTo(CREATE_TICKET_PATH))
            .withHeader(IdempotencyUtil.IDEMPOTENCY_KEY_HEADER, equalTo(TICKET_KEY)));
  }
}
