package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

/** This is the base class for implementing pagination on any SearchResultEntity */
public abstract class Page<T extends SearchResultEntity> implements Serializable {

  private static final long serialVersionUID = 456807064715979598L;

  private @JsonProperty("next_page") String nextPage;

  private @JsonProperty("previous_page") String previousPage;

  private List<T> results;

  private int count;

  public abstract Class<T> getTargetClass();

  public List<T> getResults() {
    return results;
  }

  public void setResults(final List<T> results) {
    this.results = results;
  }

  public String getNextPage() {
    return nextPage;
  }

  public void setNextPage(final String nextPage) {
    this.nextPage = nextPage;
  }

  public String getPreviousPage() {
    return previousPage;
  }

  public void setPreviousPage(final String previousPage) {
    this.previousPage = previousPage;
  }

  public int getCount() {
    return count;
  }

  public void setCount(final int count) {
    this.count = count;
  }
}
