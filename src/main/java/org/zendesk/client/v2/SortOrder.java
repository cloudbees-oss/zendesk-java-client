package org.zendesk.client.v2;

public enum SortOrder {
    ASCENDING("asc"), DESCENDING("desc");

    private String queryParameter;

    SortOrder(String queryParameter) {
        this.queryParameter = queryParameter;
    }

    public String getQueryParameter() {
        return queryParameter;
    }
}
