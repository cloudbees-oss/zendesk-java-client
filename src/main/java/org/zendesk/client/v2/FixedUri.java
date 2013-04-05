package org.zendesk.client.v2;

/**
 * @author stephenc
 * @since 05/04/2013 10:02
 */
class FixedUri extends Uri {

    private final String uri;

    FixedUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return uri;
    }
}
