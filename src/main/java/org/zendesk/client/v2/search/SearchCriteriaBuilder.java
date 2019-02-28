package org.zendesk.client.v2.search;

import java.util.Objects;

public class SearchCriteriaBuilder extends PaginationBuilder {

    private QueryCondition queryCondition;

    private SearchCriteriaBuilder() {
    }

    public static SearchCriteriaBuilder create() {
        return new SearchCriteriaBuilder();
    }

    public SearchCriteriaBuilder query(QueryCondition query) {
        queryCondition = query;
        return this;
    }

    public QueryCondition getQueryCondition() {
        return queryCondition;
    }

    public String build() {
        Objects.requireNonNull(queryCondition);

        return new StringBuilder()
                .append("/search.json?query=")
                .append(queryCondition.apply())
                .append(super.build())
                .toString();
    }
}
