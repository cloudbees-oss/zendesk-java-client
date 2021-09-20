package org.zendesk.client.v2.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class Page<T extends SearchResultEntity> implements Serializable {

  private static final long serialVersionUID = 456807064715979598L;

  private @JsonProperty("next_page") String nextPage;

  private @JsonProperty("previous_page") String previousPage;

  private int count;
  
  public abstract Class<T> getTargetClass();

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
