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
public class ChangeEvent extends CreateEvent {

    private static final long serialVersionUID = 1L;

    private List<String> previousValue;

    @JsonProperty("previous_value")
    public Object getPreviousValueObject() {
        if (previousValue == null) {
            return null;
        }
        if (previousValue.size() == 1) {
            return previousValue.get(0);
        }
        return previousValue;
    }

    public void setPreviousValueObject(Object previousValue) {
        if (previousValue == null) {
            this.previousValue = null;
        } else if (previousValue instanceof List) {
            this.previousValue = new ArrayList<>();
            for (Object o : (List<?>) previousValue) {
                this.previousValue.add(o == null || o instanceof String ? (String) o : o.toString());
            }
        } else if (previousValue instanceof String[]) {
            this.previousValue = new ArrayList<>();
            Collections.addAll(this.previousValue, (String[]) previousValue);
        } else if (previousValue instanceof Object[]) {
            this.previousValue = new ArrayList<>();
            for (Object o : (Object[]) previousValue) {
                this.previousValue.add(o == null || o instanceof String ? (String) o : o.toString());
            }
        } else if (previousValue instanceof String) {
            setPreviousValue((String) previousValue);
        } else {
            setPreviousValue(previousValue.toString());
        }
    }

    @JsonIgnore
    public List<String> getPreviousValues() {
        return previousValue;
    }

    public void setPreviousValues(List<String> previousValue) {
        this.previousValue = previousValue;
    }

    @JsonIgnore
    public String getPreviousValue() {
        return previousValue == null || previousValue.size() != 1 ? null : previousValue.get(0);
    }

    public void setPreviousValue(String previousValue) {
        if (previousValue == null) {
            this.previousValue = null;
        } else {
            this.previousValue = new ArrayList<>();
            this.previousValue.add(previousValue);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ChangeEvent");
        sb.append("{previousValue=").append(previousValue);
        sb.append('}');
        return sb.toString();
    }
}
