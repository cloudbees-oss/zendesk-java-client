package org.zendesk.client.v2;

import org.asynchttpclient.Response;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * {@link ZendeskException} specialisation for HTTP non-2xx responses
 */
public class ZendeskResponseException extends ZendeskException {

    private static final long serialVersionUID = 1L;

    private int statusCode;
    private String statusText;
    private String body;

    public ZendeskResponseException(Response resp) throws IOException {
        this(resp.getStatusCode(), resp.getStatusText(), resp.getResponseBody());
    }

    public ZendeskResponseException(int statusCode, String statusText, String body) {
        super(MessageFormat.format("HTTP/{0}: {1} - {2}", statusCode, statusText, body));
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.body = body;
    }

    public ZendeskResponseException(ZendeskResponseException cause) {
        super(cause.getMessage(), cause);
        this.statusCode = cause.getStatusCode();
        this.statusText = cause.getStatusText();
        this.body = cause.getBody();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getBody() {
        return body;
    }
}
