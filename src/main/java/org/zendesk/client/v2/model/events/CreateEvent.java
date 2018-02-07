package org.zendesk.client.v2.model.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author stephenc
 * @since 05/04/2013 11:56
 */
public class CreateEvent extends Event {

    private static final long serialVersionUID = 1L;

    private String fieldName;
    private List<String> value;

    @JsonProperty("field_name")
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @JsonProperty("value")
    public Object getValueObject() {
        if (value == null) {
            return null;
        }
        if (value.size() == 1) {
            return value.get(0);
        }
        return value;
    }

    public void setValueObject(Object value) {
        if (value == null) {
            this.value = null;
        } else if (value instanceof List) {
            this.value = new ArrayList<>();
            for (Object o : (List<?>) value) {
                this.value.add(o == null || o instanceof String ? (String) o : o.toString());
            }
        } else if (value instanceof String[]) {
            this.value = new ArrayList<>();
            Collections.addAll(this.value, (String[]) value);
        } else if (value instanceof Object[]) {
            this.value = new ArrayList<>();
            for (Object o : (Object[]) value) {
                this.value.add(o == null || o instanceof String ? (String) o : o.toString());
            }
        } else if (value instanceof String) {
            setValue((String) value);
        } else {
            setValue(value.toString());
        }
    }

    @JsonIgnore
    public List<String> getValues() {
        return value;
    }

    public void setValues(List<String> value) {
        this.value = value;
    }

    @JsonIgnore
    public String getValue() {
        return value == null || value.size() != 1 ? null : value.get(0);
    }

    public void setValue(String value) {
        if (value == null) {
            this.value = null;
        } else {
            this.value = new ArrayList<>();
            this.value.add(value);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CreateEvent");
        sb.append("{fieldName='").append(fieldName).append('\'');
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
