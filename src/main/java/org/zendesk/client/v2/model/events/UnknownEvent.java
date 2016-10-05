package org.zendesk.client.v2.model.events;

public class UnknownEvent extends Event {

    private static final long serialVersionUID = 1L;

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("UnknownEvent");
        sb.append("{id=").append(getId());
        sb.append(", type=\"").append(getType()).append("\"");
        sb.append('}');
        return sb.toString();
    }
}
