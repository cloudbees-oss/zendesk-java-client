package org.zendesk.client.v2.model;

public enum TopicType {
    ARTICLES("Articles"),
    QUESTIONS("Questions"),
    IDEAS("Ideas");

    private final String name;

    TopicType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}