package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Arrays;

/**
 * @author adavidson
 * @author Johno Crawford (johno@sulake.com)
 */
public class Action {

    private String field;

    @JsonFormat(with = {JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
            JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED})
    private Object[] value;

    public Action() {
    }

    public Action(String field, String[] value) {
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object[] getValue() {
        return value;
    }

    public void setValue(Object[] value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Action{" +
                "field='" + field + '\'' +
                ", value=" + Arrays.toString(value) +
                '}';
    }
}
