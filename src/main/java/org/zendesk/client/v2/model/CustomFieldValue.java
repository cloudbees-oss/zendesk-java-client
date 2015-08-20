package org.zendesk.client.v2.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author stephenc
 * @since 04/04/2013 14:53
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomFieldValue implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String value;

    public CustomFieldValue() {
    }

    public CustomFieldValue(Long id, String value) {
        this.id = id;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
