package org.zendesk.client.v2.model;

/**
 * An enum that represents sorting order. The name value maps to what the Zendesk API actually 
 * expects in the query param.
 * @author rbolles on 2/7/18.
 */
public enum SortOrder {
    ASCENDING("asc"),
    DESCING("desc");

    private final String name;

    SortOrder(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
