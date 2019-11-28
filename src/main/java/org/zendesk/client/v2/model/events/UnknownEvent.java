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
        return "UnknownEvent" +
                "{id=" + getId() +
                ", type=\"" + getType() + "\"" +
                '}';
    }
}
