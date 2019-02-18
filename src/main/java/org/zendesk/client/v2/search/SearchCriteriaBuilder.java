package org.zendesk.client.v2.search;

import static java.util.Optional.ofNullable;

public class SearchCriteriaBuilder {

    private QueryCondition queryCondition;
    private SortingCondition sortingCondition = new SortingCondition();
    private PageCondition pageCondition = new PageCondition();

    private SearchCriteriaBuilder() {
    }

    public static SearchCriteriaBuilder searchCriteria() {
        return new SearchCriteriaBuilder();
    }

    public SearchCriteriaBuilder query(QueryCondition query) {
        queryCondition = query;
        return this;
    }

    public SearchCriteriaBuilder sortOrder(SortingCondition.SortOrder sortOrder) {
        sortingCondition.setSortOrder(sortOrder);
        return this;
    }

    public SearchCriteriaBuilder sortBy(SortingCondition.SortBy sortBy) {
        sortingCondition.setSortBy(sortBy);
        return this;
    }

    public SearchCriteriaBuilder page(Integer page) {
        pageCondition.setPage(page);
        return this;
    }

    public SearchCriteriaBuilder perPage(Integer perPage) {
        pageCondition.setPerPage(perPage);
        return this;
    }

    public QueryCondition getQueryCondition() {
        return queryCondition;
    }

    public SortingCondition getSortingCondition() {
        return sortingCondition;
    }

    public PageCondition getPageCondition() {
        return pageCondition;
    }

    public String build() {

        StringBuilder query = new StringBuilder();

        ofNullable(queryCondition).ifPresent(c -> query.append("/search.json?query=").append(c.apply()));
        ofNullable(sortingCondition.getSortBy()).ifPresent(c -> query.append("&sort_by=").append(c));
        ofNullable(sortingCondition.getSortOrder()).ifPresent(c -> query.append("&sort_order=").append(c));
        ofNullable(pageCondition.getPage()).ifPresent(c -> query.append("&page=").append(c));
        ofNullable(pageCondition.getPerPage()).ifPresent(c -> query.append("&per_page=").append(c));

        return query.toString();
    }
}
