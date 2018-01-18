package org.zendesk.client.v2.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        String json = "{ \"ticket\": { \"id\": 21337631753}}";
        TicketResult ev = parseJson(json.getBytes());
        assertNotNull(ev);
        assertEquals(TicketResult.class, ev.getClass());
    }

}
