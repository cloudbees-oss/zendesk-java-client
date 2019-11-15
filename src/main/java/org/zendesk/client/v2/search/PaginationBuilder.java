package org.zendesk.client.v2.search;

import static java.util.Optional.ofNullable;

public class PaginationBuilder {

    private SortingCondition sortingCondition = new SortingCondition();
    private PageCondition pageCondition = new PageCondition();

    protected PaginationBuilder() {
    }

    public static PaginationBuilder create() {
        return new PaginationBuilder();
    }

    public PaginationBuilder sortOrder(SortingCondition.SortOrder sortOrder) {
        sortingCondition.setSortOrder(sortOrder);
        return this;
    }

    public PaginationBuilder sortBy(SortingCondition.SortBy sortBy) {
        sortingCondition.setSortBy(sortBy);
        return this;
    }

    public PaginationBuilder page(Integer page) {
        pageCondition.setPage(page);
        return this;
    }

    public PaginationBuilder perPage(Integer perPage) {
        pageCondition.setPerPage(perPage);
        return this;
    }

    public SortingCondition getSortingCondition() {
        return sortingCondition;
    }

    public PageCondition getPageCondition() {
        return pageCondition;
    }



    public String build() {

        StringBuilder query = new StringBuilder();

        ofNullable(sortingCondition.getSortBy()).ifPresent(c -> query.append("&sort_by=").append(c));
        ofNullable(sortingCondition.getSortOrder()).ifPresent(c -> query.append("&sort_order=").append(c));
        ofNullable(pageCondition.getPage()).ifPresent(c -> query.append("&page=").append(c));
        ofNullable(pageCondition.getPerPage()).ifPresent(c -> query.append("&per_page=").append(c));

        return query.toString();
    }
}
