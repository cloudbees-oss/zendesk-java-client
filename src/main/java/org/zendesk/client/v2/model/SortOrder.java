package org.zendesk.client.v2.model;

/**
 * An enum that represents sorting order. The name value maps to what the Zendesk API actually 
 * expects in the query param.
 * @author rbolles on 2/7/18.
 */
public enum SortOrder {
    ASCENDING("asc"), DESCENDING("desc");

    private final String queryParameter;

    SortOrder(String queryParameter) {
        this.queryParameter = queryParameter;
    }

    public String getQueryParameter() {
        return queryParameter;
    }
}
