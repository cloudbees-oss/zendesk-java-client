package org.zendesk.client.v2.model.hc;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.Objects;

/**
 * You can assign a content tag to posts and articles to loosely group them together. For more
 * information, see <a href="https://support.zendesk.com/hc/en-us/articles/4848925672730">About
 * Content tags</a> in Zendesk help.
 */
public class ContentTag {

  /**
   * Automatically assigned when the content tag is created. N.B. unlike many other entities, the id
   * field is a String, not a Long
   */
  private String id;

  /** The name of the content tag */
  private String name;

  /** The time the content tag was created */
  @JsonProperty("created_at")
  private Date createdAt;

  /** The time the content tag was last updated */
  @JsonProperty("updated_at")
  private Date updatedAt;

  public ContentTag() {}

  public ContentTag(String id, String name, Date createdAt, Date updatedAt) {
    this.id = id;
    this.name = name;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ContentTag that = (ContentTag) o;
    return Objects.equals(id, that.id)
        && Objects.equals(name, that.name)
        && Objects.equals(createdAt, that.createdAt)
        && Objects.equals(updatedAt, that.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    return "ContentTag{"
        + "id='"
        + id
        + '\''
        + ", name='"
        + name
        + '\''
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + '}';
  }
}
