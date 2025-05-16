package org.zendesk.client.v2.model.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

// this is somewhat similar to a Page but with a single ExecuteView object and not a list
// hence why we cannot inherite Page<?>
public class ExecutedViewPage<V extends ViewRow> implements Serializable {
  private static final long serialVersionUID = -7683989282890282540L;

  private @JsonProperty("next_page") String nextPage;
  private @JsonProperty("previous_page") String previousPage;
  private int count;

  private List<ViewColumn> columns;
  private List<V> rows;

  public List<ViewColumn> getColumns() {
    return columns;
  }

  public void setColumns(List<ViewColumn> columns) {
    this.columns = columns;
  }

  public List<V> getRows() {
    return rows;
  }

  public void setRows(List<V> rows) {
    this.rows = rows;
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

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;

    ExecutedViewPage<?> that = (ExecutedViewPage<?>) o;
    return count == that.count
        && Objects.equals(nextPage, that.nextPage)
        && Objects.equals(previousPage, that.previousPage)
        && Objects.equals(columns, that.columns)
        && Objects.equals(rows, that.rows);
  }

  @Override
  public int hashCode() {
    int result = Objects.hashCode(nextPage);
    result = 31 * result + Objects.hashCode(previousPage);
    result = 31 * result + count;
    result = 31 * result + Objects.hashCode(columns);
    result = 31 * result + Objects.hashCode(rows);
    return result;
  }

  @Override
  public String toString() {
    return "ExecutedViewPage{"
        + "nextPage='"
        + nextPage
        + '\''
        + ", previousPage='"
        + previousPage
        + '\''
        + ", count="
        + count
        + ", columns="
        + columns
        + ", rows="
        + rows
        + '}';
  }
}
