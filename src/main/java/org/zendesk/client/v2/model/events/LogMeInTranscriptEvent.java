package org.zendesk.client.v2.model.events;

/**
 * @author stephenc
 * @since 05/04/2013 11:57
 */
public class LogMeInTranscriptEvent extends Event {

    private static final long serialVersionUID = 1L;

    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("LogMeInTranscriptEvent");
        sb.append("{body='").append(body).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
