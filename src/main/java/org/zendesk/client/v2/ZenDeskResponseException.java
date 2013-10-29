package org.zendesk.client.v2;

import java.io.IOException;

import com.ning.http.client.Response;

/**
 * {@link ZenDeskException} specialisation for HTTP non-2xx responses
 */
public class ZenDeskResponseException extends ZenDeskException {
    private int statusCode;
    private String statusText;
    private String body;

    public ZenDeskResponseException(Response resp) throws IOException {
	this(resp.getStatusCode(), resp.getStatusText(), resp.getResponseBody());
    }
    
    public ZenDeskResponseException(int statusCode, String statusText, String body) {
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
