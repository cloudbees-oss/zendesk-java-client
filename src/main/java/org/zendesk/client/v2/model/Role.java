package org.zendesk.client.v2.model;

public enum Role {
    END_USER("end-user"),
    AGENT("agent"),
    ADMIN("admin");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

