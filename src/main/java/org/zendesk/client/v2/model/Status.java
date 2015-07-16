package org.zendesk.client.v2.model;

/**
 * @author stephenc
 * @since 05/04/2013 08:56
 */
public enum Status {
    NEW,
    OPEN,
    PENDING,
    HOLD,
    SOLVED,
    CLOSED,
    DELETED;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

}
