package org.zendesk.client.v2.model;

import java.util.Objects;

public class Tag {

  private String name;

  private Integer count;

  public Tag() {}

  public Tag(String name, Integer count) {
    this.name = name;
    this.count = count;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Tag tag = (Tag) o;
    return Objects.equals(name, tag.name) && Objects.equals(count, tag.count);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, count);
  }

  @Override
  public String toString() {
    return "Tag{" + "name='" + name + '\'' + ", count=" + count + '}';
  }
}
