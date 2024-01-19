package org.zendesk.client.v2.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class TicketResultTest {

  private TicketResult parseJson(byte[] json) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(json, TicketResult.class);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Test
  public void testParseTicketResult() {
    String json = "{ \"ticket\": { \"id\": 21337631753, \"custom_status_id\": 9999}}";
    TicketResult ev = parseJson(json.getBytes());
    assertNotNull(ev);
    assertEquals(Long.valueOf(9999), ev.getTicket().getCustomStatusId());
    assertEquals(TicketResult.class, ev.getClass());
  }
}
