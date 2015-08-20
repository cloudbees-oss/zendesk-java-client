package org.zendesk.client.v2.model.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author stephenc
 * @since 05/04/2013 11:57
 */
public class TweetEvent extends Event {

    private static final long serialVersionUID = 1L;

    private Boolean directMessage;
    private String body;
    private List<Long> recipients;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @JsonProperty("direct_message")
    public Boolean getDirectMessage() {
        return directMessage;
    }

    public void setDirectMessage(Boolean directMessage) {
        this.directMessage = directMessage;
    }

    public List<Long> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Long> recipients) {
        this.recipients = recipients;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("TweetEvent");
        sb.append("{body='").append(body).append('\'');
        sb.append(", directMessage=").append(directMessage);
        sb.append(", recipients=").append(recipients);
        sb.append('}');
        return sb.toString();
    }
}
