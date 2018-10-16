package org.zendesk.client.v2.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.zendesk.client.v2.Utils;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TriggerTest {

    private Trigger parseJson(byte[] json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, Trigger.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte [] writeJson(Trigger trigger) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsBytes(trigger);
    }

    @Test
    public void testTriggerWithComplexActionValues() throws Exception {
        String json = Utils.resourceToString(getClass().getName().replace('.', '/') + "/triggerWithComplexActions.json");
        Trigger trigger = parseJson(json.getBytes());
        assertNotNull(trigger);
        assertEquals("Test Trigger", trigger.getTitle());
        assertEquals(1, trigger.getActions().size());
        assertEquals("notification_target", trigger.getActions().get(0).getField());
        assertArrayEquals(new Object [] {"1234567890", Arrays.asList(Arrays.asList("key1", "value1"), Arrays.asList("key2", "value2"))}, trigger.getActions().get(0).getValue());
        assertEquals(0, trigger.getConditions().getAll().size());
        assertEquals(1, trigger.getConditions().getAny().size());
        assertEquals("comment_includes_word", trigger.getConditions().getAny().get(0).getField());
        assertEquals("includes", trigger.getConditions().getAny().get(0).getOperator());
        assertEquals("@triggerTest", trigger.getConditions().getAny().get(0).getValue());
        assertEquals(json, new String(writeJson(trigger)));
    }

    @Test
    public void testTriggerWithMultilineActionValues() throws Exception {
        String json = Utils.resourceToString(getClass().getName().replace('.', '/') + "/triggerWithMultilineAction.json");
        Trigger trigger = parseJson(json.getBytes());
        assertNotNull(trigger);
        assertEquals("Test Trigger", trigger.getTitle());
        assertEquals(2, trigger.getActions().size());
        assertEquals("notification_user", trigger.getActions().get(0).getField());
        assertArrayEquals(new String [] {"09876543", "Trigger Target 1 {{ticket.title}}", "Test Multiline\n\n{{ticket.comments_formatted}}\n\n--------------------------"}, trigger.getActions().get(0).getValue());
        assertEquals("notification_target", trigger.getActions().get(1).getField());
        assertArrayEquals(new String [] {"98765432", "{\n  \"color\":\"purple\",\n  \"message_format\":\"text\",\n  \"message\":\"Trigger Target 2 {{ticket.title}}.\"\n}"}, trigger.getActions().get(1).getValue());
        assertEquals(0, trigger.getConditions().getAll().size());
        assertEquals(1, trigger.getConditions().getAny().size());
        assertEquals("comment_includes_word", trigger.getConditions().getAny().get(0).getField());
        assertEquals("includes", trigger.getConditions().getAny().get(0).getOperator());
        assertEquals("@triggerTest", trigger.getConditions().getAny().get(0).getValue());
        assertEquals(json, new String(writeJson(trigger)));
    }
}
