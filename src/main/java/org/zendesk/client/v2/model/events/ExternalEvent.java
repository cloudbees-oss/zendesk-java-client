package org.zendesk.client.v2.model.events;

/**
 * @author stephenc
 * @since 05/04/2013 11:57
 */
public class ExternalEvent extends Event {

    private static final long serialVersionUID = 1L;

    private String resource;
    private String body;
    private String success;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "ExternalEvent" +
                "{body='" + body + '\'' +
                ", resource='" + resource + '\'' +
                ", success='" + success + '\'' +
                '}';
    }
}
