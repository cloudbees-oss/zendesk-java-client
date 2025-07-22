package org.zendesk.client.v2.model.views;

import java.io.Serializable;
import java.util.Objects;

public class ViewColumn implements Serializable {
  private static final long serialVersionUID = 5250249523833634470L;

  private String id;
  private String title;
  private boolean filterable;
  private boolean sortable;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean isFilterable() {
    return filterable;
  }

  public void setFilterable(boolean filterable) {
    this.filterable = filterable;
  }

  public boolean isSortable() {
    return sortable;
  }

  public void setSortable(boolean sortable) {
    this.sortable = sortable;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;

    ViewColumn that = (ViewColumn) o;
    return filterable == that.filterable
        && sortable == that.sortable
        && Objects.equals(id, that.id)
        && Objects.equals(title, that.title);
  }

  @Override
  public int hashCode() {
    int result = Objects.hashCode(id);
    result = 31 * result + Objects.hashCode(title);
    result = 31 * result + Boolean.hashCode(filterable);
    result = 31 * result + Boolean.hashCode(sortable);
    return result;
  }

  @Override
  public String toString() {
    return "ViewColumn{"
        + "id='"
        + id
        + '\''
        + ", title='"
        + title
        + '\''
        + ", filterable="
        + filterable
        + ", sortable="
        + sortable
        + '}';
  }
}
