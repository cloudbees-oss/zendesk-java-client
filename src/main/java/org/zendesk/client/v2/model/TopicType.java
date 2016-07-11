package org.zendesk.client.v2.model;

public enum TopicType {
    ARTICLES("articles"),
    QUESTIONS("questions"),
    IDEAS("ideas");

    private final String name;

    private TopicType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
