package org.zendesk.client.v2.search;

public class PageCondition {
    private Integer page;
    private Integer perPage;

    public Integer getPage() {
        return page;
    }

    public PageCondition setPage(Integer page) {
        this.page = page;
        return this;
    }

    public Integer getPerPage() {
        return perPage;
    }

    public PageCondition setPerPage(Integer perPage) {
        this.perPage = perPage;
        return this;
    }
}
