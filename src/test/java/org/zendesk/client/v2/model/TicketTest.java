package org.zendesk.client.v2.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import org.junit.Test;
import org.zendesk.client.v2.Zendesk;

public class TicketTest {

  private static final long TICKET_ID = 12345;
  private static final String TICKET_SUBJECT = "Test subject";
  private static final Status TICKET_STATUS = Status.OPEN;
  private static final Date TICKET_TS = new Date();

  private static final Map<String, Object> TICKET_JSON_MAP = Map.of(
      "id", TICKET_ID,
      "subject", TICKET_SUBJECT,
      "status", TICKET_STATUS.toString(),
      "updated_at", new StdDateFormat().format(TICKET_TS),
      "has_incidents", false);

  private static final String TICKET_IDEMPOTENCY_KEY = "test-key-123";

  private static final ObjectMapper OBJECT_MAPPER = Zendesk.createMapper(Function.identity());

  @Test
  public void serializeWithNullSafeUpdate() throws Exception {
    Ticket ticket = createSampleTicket();
    assertThat(OBJECT_MAPPER.writeValueAsString(ticket))
        .doesNotContain("\"safe_update\"")
        .doesNotContain("\"updated_stamp\"");
  }

  @Test
  public void serializeWithFalseSafeUpdate() throws Exception {
    Ticket ticket = createSampleTicket();
    ticket.setSafeUpdate(false);
    assertThat(OBJECT_MAPPER.writeValueAsString(ticket))
        .doesNotContain("\"safe_update\"")
        .doesNotContain("\"updated_stamp\"");
  }

  @Test
  public void serializeWithSafeUpdate() throws Exception {
    Ticket ticket = createSampleTicket();
    ticket.setSafeUpdate(true);
    assertThat(OBJECT_MAPPER.writeValueAsString(ticket))
        .contains("\"safe_update\"")
        .contains("\"updated_stamp\"");
  }

  @Test
  public void idempotencyFields_areNotSerialized() throws Exception {
    Ticket ticket = createSampleTicket();
    ticket.setIdempotencyKey("test-idempotency-key");
    ticket.setIsIdempotencyHit(true);

    String json = OBJECT_MAPPER.writeValueAsString(ticket);
    Map<?, ?> jsonMap = OBJECT_MAPPER.readValue(json, Map.class);
    assertThat(jsonMap).isEqualTo(TICKET_JSON_MAP);
  }

  @Test
  public void ticket_canBeDeserializedWithoutIdempotencyFields() throws Exception {
    String json = OBJECT_MAPPER.writeValueAsString(TICKET_JSON_MAP);
    Ticket ticket = OBJECT_MAPPER.readValue(json, Ticket.class);

    assertThat(ticket).isNotNull();
    assertThat(ticket.getId()).isEqualTo(TICKET_ID);
    assertThat(ticket.getSubject()).isEqualTo(TICKET_SUBJECT);
    assertThat(ticket.getStatus()).isEqualTo(TICKET_STATUS);
    assertThat(ticket.getIdempotencyKey()).isNull();
    assertThat(ticket.getIsIdempotencyHit()).isNull();
  }

  @Test
  public void idempotencyKey_getterSetterWork() {
    Ticket ticket = createSampleTicket();
    ticket.setIdempotencyKey(TICKET_IDEMPOTENCY_KEY);

    assertThat(ticket.getIdempotencyKey()).isEqualTo(TICKET_IDEMPOTENCY_KEY);
  }

  @Test
  public void isIdempotencyHit_getterSetterWork() {
    Ticket ticket = createSampleTicket();

    ticket.setIsIdempotencyHit(true);
    assertThat(ticket.getIsIdempotencyHit()).isTrue();

    ticket.setIsIdempotencyHit(false);
    assertThat(ticket.getIsIdempotencyHit()).isFalse();

    ticket.setIsIdempotencyHit(null);
    assertThat(ticket.getIsIdempotencyHit()).isNull();
  }

  private Ticket createSampleTicket() {
    Ticket ticket = new Ticket();
    ticket.setId(TICKET_ID);
    ticket.setSubject(TICKET_SUBJECT);
    ticket.setStatus(TICKET_STATUS);
    ticket.setUpdatedAt(TICKET_TS);
    return ticket;
  }
}
