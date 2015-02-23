package org.zendesk.client.v2.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Zendesk sometimes returns JSON with a String instead of String[] so we need a custom deserializer.
 *
 * @author adavidson
 * @author Johno Crawford (johno@sulake.com)
 */
@JsonDeserialize(using = Action.ActionDeserializer.class)
public class Action {

    private String field;
    private List<String> value;

    public Action() {
    }

    public Action(String field, String... values) {
        this.field = field;
        this.value = Arrays.asList(values);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Action");
        sb.append("{field=").append(field);
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }

    static class ActionDeserializer extends JsonDeserializer<Action> {

        @Override
        public Action deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            JsonNode root = jsonParser.getCodec().readTree(jsonParser);
            Action action = new Action();
            action.field = root.get("field").asText();

            JsonNode valueNode = root.get("value");
            if (valueNode.isArray()) {
                List<String> values = new ArrayList<String>(valueNode.size());
                for (final JsonNode node : valueNode) {
                    values.add(node.asText());
                }
                action.value = values;
            } else {
                action.value = Collections.singletonList(valueNode.asText());
            }
            return action;
        }
    }

}
