package org.zendesk.client.v2.model.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author stephenc
 * @since 05/04/2013 11:57
 */
public class PushEvent extends Event {

    private static final long serialVersionUID = 1L;

    private String value;
    private String valueReference;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty("value_reference")
    public String getValueReference() {
        return valueReference;
    }

    public void setValueReference(String valueReference) {
        this.valueReference = valueReference;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("PushEvent");
        sb.append("{value='").append(value).append('\'');
        sb.append(", valueReference='").append(valueReference).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
