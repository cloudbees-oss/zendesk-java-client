package org.zendesk.client.v2.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import org.junit.Test;
import org.zendesk.client.v2.Zendesk;

public class TicketTest {

  private static final Random RANDOM = new Random();
  private static final String TICKET_COMMENT1 = "Please ignore this ticket";
  private static final Date NOW = Calendar.getInstance().getTime();

  @Test
  public void serializeWithNullSafeUpdate() throws Exception {
    ObjectMapper mapper = Zendesk.createMapper();
    Ticket ticket = createSampleTicket();
    assertThat(mapper.writeValueAsString(ticket))
        .doesNotContain("\"safe_update\"")
        .doesNotContain("\"updated_stamp\"");
  }

  @Test
  public void serializeWithFalseSafeUpdate() throws Exception {
    ObjectMapper mapper = Zendesk.createMapper();
    Ticket ticket = createSampleTicket();
    ticket.setSafeUpdate(false);
    assertThat(mapper.writeValueAsString(ticket))
        .doesNotContain("\"safe_update\"")
        .doesNotContain("\"updated_stamp\"");
  }

  @Test
  public void serializeWithSafeUpdate() throws Exception {
    ObjectMapper mapper = Zendesk.createMapper();
    Ticket ticket = createSampleTicket();
    ticket.setSafeUpdate(true);
    assertThat(mapper.writeValueAsString(ticket))
        .contains("\"safe_update\"")
        .contains("\"updated_stamp\"");
  }

  private Ticket createSampleTicket() {
    Ticket ticket = new Ticket();
    ticket.setId(Math.abs(RANDOM.nextLong()));
    ticket.setComment(new Comment(TICKET_COMMENT1));
    ticket.setUpdatedAt(NOW);
    ticket.setCustomStatusId(Math.abs(RANDOM.nextLong()));
    return ticket;
  }
}
