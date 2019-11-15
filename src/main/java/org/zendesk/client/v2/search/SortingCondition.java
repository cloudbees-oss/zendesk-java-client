package org.zendesk.client.v2.search;

public class SortingCondition {

    public enum SortOrder {
        ASC, DESC;

        public static SortBy fromString(String key) {
            for(SortBy type : SortBy.values()) {
                if(type.name().equalsIgnoreCase(key)) {
                    return type;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public enum SortBy {
        UPDATED_AT, CREATED_AT, PRIORITY, STATUS, TICKET_TYPE;

        public static SortBy fromString(String key) {
            for(SortBy type : SortBy.values()) {
                if(type.name().equalsIgnoreCase(key)) {
                    return type;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    private SortBy sortBy;
    private SortOrder sortOrder;

    public SortBy getSortBy() {
        return sortBy;
    }

    public SortingCondition setSortBy(SortBy sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public SortingCondition setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }
}
