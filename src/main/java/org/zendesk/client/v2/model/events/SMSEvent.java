package org.zendesk.client.v2.model.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author stephenc
 * @since 05/04/2013 11:57
 */
public class SMSEvent extends Event {

    private static final long serialVersionUID = 1L;

    private String body;
    private String phoneNumber;
    private Long recipientId;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @JsonProperty("phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @JsonProperty("recipient_id")
    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SMSEvent");
        sb.append("{body='").append(body).append('\'');
        sb.append(", phoneNumber='").append(phoneNumber).append('\'');
        sb.append(", recipientId=").append(recipientId);
        sb.append('}');
        return sb.toString();
    }
}
