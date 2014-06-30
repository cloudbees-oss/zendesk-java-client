package org.zendesk.client.v2;

import java.io.IOException;

import com.ning.http.client.Response;

/**
 * {@link Zendesk_Exception} specialisation for HTTP non-2xx responses
 */
public class Zendesk_ResponseException extends Zendesk_Exception {
    private int statusCode;
    private String statusText;
    private String body;

    public Zendesk_ResponseException(Response resp) throws IOException {
	this(resp.getStatusCode(), resp.getStatusText(), resp.getResponseBody());
    }
    
    public Zendesk_ResponseException(int statusCode, String statusText, String body) {
        super(statusText);
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.body = body;
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
