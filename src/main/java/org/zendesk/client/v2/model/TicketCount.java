package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketCount {

    private Long value;
    private Date refreshedAt;

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    @JsonProperty("refreshed_at")
    public Date getRefreshedAt() {
        return refreshedAt;
    }

    public void setRefreshedAt(Date refreshedAt) {
        this.refreshedAt = refreshedAt;
    }
}
