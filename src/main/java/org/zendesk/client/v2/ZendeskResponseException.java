package org.zendesk.client.v2;

import com.ning.http.client.Response;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * {@link ZendeskException} specialisation for HTTP non-2xx responses
 */
public class ZendeskResponseException extends ZendeskException {

    private static final long serialVersionUID = 1L;

    private static final int NOT_FOUND_STATUS = 404;

    private int statusCode;
    private String statusText;
    private String body;

    ZendeskResponseException(Response resp) throws IOException {
        this(resp.getStatusCode(), resp.getStatusText(), resp.getResponseBody());
    }

    private ZendeskResponseException(int statusCode, String statusText, String body) {
        super(MessageFormat.format("HTTP/{0}: {1}", statusCode, statusText));
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.body = body;
    }

    public static ZendeskResponseException fromResponse(Response resp) throws IOException {
        if (resp.getStatusCode() == NOT_FOUND_STATUS) {
            return new ZendeskEntityNotFoundException(resp);
        } else {
            return new ZendeskResponseException(resp);
        }
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
