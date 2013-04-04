package org.zendesk.client.v2;

/**
 * @author stephenc
 * @since 04/04/2013 14:24
 */
public class ZenDeskException extends RuntimeException {
    public ZenDeskException(String message) {
        super(message);
    }

    public ZenDeskException() {
    }

    public ZenDeskException(Throwable cause) {
        super(cause);
    }

    public ZenDeskException(String message, Throwable cause) {
        super(message, cause);
    }
}
