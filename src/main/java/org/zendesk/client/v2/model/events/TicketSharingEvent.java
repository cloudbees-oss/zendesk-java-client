package org.zendesk.client.v2.model.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author stephenc
 * @since 05/04/2013 11:57
 */
public class TicketSharingEvent extends Event {

    private static final long serialVersionUID = 1L;

    private Long agreementId;
    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @JsonProperty("agreement_id")
    public Long getAgreementId() {
        return agreementId;
    }

    public void setAgreementId(Long agreementId) {
        this.agreementId = agreementId;
    }

    @Override
    public String toString() {
        return "TicketSharingEvent" +
                "{action='" + action + '\'' +
                ", agreementId=" + agreementId +
                '}';
    }
}
