package org.zendesk.client.v2.model.events;

/**
 * @author stephenc
 * @since 05/04/2013 11:56
 */
public class NotificationEvent extends CcEvent {

    private static final long serialVersionUID = 1L;

    private String subject;
    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "NotificationEvent" +
                "{body='" + body + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}
