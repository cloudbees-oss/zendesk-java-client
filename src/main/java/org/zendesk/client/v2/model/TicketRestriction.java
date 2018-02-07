package org.zendesk.client.v2.model;

public enum TicketRestriction {
    ORGANIZATION("organization"),
    GROUPS("groups"),
    ASSIGNED("assigned"),
    REQUESTED("requested");

    private final String name;

    TicketRestriction(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
