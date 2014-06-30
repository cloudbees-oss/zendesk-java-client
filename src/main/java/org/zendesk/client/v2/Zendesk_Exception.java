package org.zendesk.client.v2;

/**
 * @author stephenc
 * @since 04/04/2013 14:24
 */
public class Zendesk_Exception extends RuntimeException {
    public Zendesk_Exception(String message) {
        super(message);
    }

    public Zendesk_Exception() {
    }

    public Zendesk_Exception(Throwable cause) {
        super(cause);
    }

    public Zendesk_Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
