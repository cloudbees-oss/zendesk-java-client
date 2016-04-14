package org.zendesk.client.v2.model.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * @author stephenc
 * @since 05/04/2013 11:57
 */
public class FacebookEvent extends Event {

    private static final long serialVersionUID = 1L;

    private Map<String, Object> page;
    private Long communication;
    private String ticketVia;
    private String body;

    public Long getCommunication() {
        return communication;
    }

    public void setCommunication(Long communication) {
        this.communication = communication;
    }

    public Map<String, Object> getPage() {
        return page;
    }

    public void setPage(Map<String, Object> page) {
        this.page = page;
    }

    @JsonProperty("ticket_via")
    public String getTicketVia() {
        return ticketVia;
    }

    public void setTicketVia(String ticketVia) {
        this.ticketVia = ticketVia;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("FacebookEvent");
        sb.append("{body='").append(body).append('\'');
        sb.append(", page=").append(page);
        sb.append(", communication=").append(communication);
        sb.append(", ticketVia='").append(ticketVia).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
