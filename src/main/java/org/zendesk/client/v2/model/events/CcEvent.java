package org.zendesk.client.v2.model.events;

import org.zendesk.client.v2.model.Via;

import java.util.List;

/**
 * @author stephenc
 * @since 05/04/2013 11:56
 */
public class CcEvent extends Event {

    private static final long serialVersionUID = 1L;

    private List<Long> recipients;
    private Via via;

    public List<Long> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Long> recipients) {
        this.recipients = recipients;
    }

    public Via getVia() {
        return via;
    }

    public void setVia(Via via) {
        this.via = via;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CcEvent");
        sb.append("{recipients=").append(recipients);
        sb.append(", via=").append(via);
        sb.append('}');
        return sb.toString();
    }
}
