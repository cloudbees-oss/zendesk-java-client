package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * There "production" code does not contain a class that represents the
 *
 * @author rbolles on 2/8/18.
 */
public class SearchResults<T> {

    private List<T> results;

    @JsonProperty("next_page")
    private String nextPage;
    @JsonProperty("prev_page")
    private String prevPage;
    private int count;

    public List<T> getResults() {
        return results;
    }

    public SearchResults<T> setResults(List<T> results) {
        this.results = results;
        return this;
    }


    public String getNextPage() {
        return nextPage;
    }

    public SearchResults<T> setNextPage(String nextPage) {
        this.nextPage = nextPage;
        return this;
    }

    public String getPrevPage() {
        return prevPage;
    }

    public SearchResults<T> setPrevPage(String prevPage) {
        this.prevPage = prevPage;
        return this;
    }

    public int getCount() {
        return count;
    }

    public SearchResults<T> setCount(int count) {
        this.count = count;
        return this;
    }
}
