package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author stephenc
 * @since 04/04/2013 14:53
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomFieldValue {
    private Integer id;
    private String value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
