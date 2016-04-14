package org.zendesk.client.v2.model.events;

/**
 * @author stephenc
 * @since 05/04/2013 11:56
 */
public class ErrorEvent extends Event {

    private static final long serialVersionUID = 1L;

    // for Errors
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ErrorEvent");
        sb.append("{message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
