package org.zendesk.client.v2.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ActionTest {

    private Action parseJson(byte[] json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, Action.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    public void testActionWithSingleValue() throws Exception {
        String json = "{ \"field\": 21337631753, \"value\": \"huuhaa\" }";
        Action action = parseJson(json.getBytes());
        assertNotNull(action);
        assertEquals(1, action.getValue().length);
        assertEquals("huuhaa", action.getValue()[0]);
    }

    @Test
    public void testActionWithValues() throws Exception {
        String json = "{ \"field\": 21337631753, \"value\": [\"huu\", \"haa\"] }";
        Action action = parseJson(json.getBytes());
        assertNotNull(action);
        assertEquals(2, action.getValue().length);
        assertEquals("huu", action.getValue()[0]);
        assertEquals("haa", action.getValue()[1]);
    }
}
