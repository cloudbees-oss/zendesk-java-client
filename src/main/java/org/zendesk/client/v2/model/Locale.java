package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;

/**
 * See <a
 * href="https://developer.zendesk.com/api-reference/ticketing/account-configuration/locales/">
 * Locales in Zendesk API </a>
 *
 * @since FIXME
 */
public class Locale implements Serializable {

  private static final long serialVersionUID = 1L;

  private Date createdAt;
  private Long id;
  private String locale;
  private String name;
  private Date updatedAt;
  private String url;

  @JsonProperty("created_at")
  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("updated_at")
  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public String toString() {
    return "Locale{"
        + "createdAt="
        + createdAt
        + ", id="
        + id
        + ", locale='"
        + locale
        + '\''
        + ", name='"
        + name
        + '\''
        + ", updatedAt="
        + updatedAt
        + ", url='"
        + url
        + '\''
        + '}';
  }
}
