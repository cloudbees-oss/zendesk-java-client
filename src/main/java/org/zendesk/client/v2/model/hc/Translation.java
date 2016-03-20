package org.zendesk.client.v2.model.hc;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Translation {
  /* Automatically assigned when a translation is created */
  private Long id;

  /* The API url of the translation */
  private String url;

  /* The url of the translation in Help Center */
  @JsonProperty("html_url")
  private String htmlUrl;

  /* The id of the item that has this translation */
  @JsonProperty("source_id")
  private Long sourceId;

  /* The type of the item that has this translation. Can be Article, Section, orCategory */
  @JsonProperty("source_type")
  private String sourceType;

  /* The locale of the translation */
  private String locale;

  /* The title of the translation */
  private String title;

  /* The body of the translation. Empty by default */
  private String body;

  /* True if the translation is outdated; false otherwise. False by default */
  private boolean outdated;

  /* True if the translation is a draft; false otherwise. False by default */
  private boolean draft;

  /* The time at which the translation was created */
  @JsonProperty("created_at")
  private Date createdAt;

  /* The time at which the translation was last updated */
  @JsonProperty("updated_at")
  private Date updatedAt;

  /* The id of the user who last updated the translation */
  @JsonProperty("updated_by_id")
  private Long updatedById;

  /* The id of the user who created the translation */
  @JsonProperty("created_by_id")
  private Long createdById;

  public Long getId() {
      return id;
  }

  public void setId( Long id ) {
      this.id = id;
  }

  public String getUrl() {
      return url;
  }

  public void setUrl( String url ) {
      this.url = url;
  }

  public String getHtmlUrl() {
      return htmlUrl;
  }

  public void setHtmlUrl( String htmlUrl ) {
      this.htmlUrl = htmlUrl;
  }

  public Long getSourceId() {
      return sourceId;
  }

  public void setSourceId( Long sourceId ) {
      this.sourceId = sourceId;
  }

  public String getSourceType() {
      return sourceType;
  }

  public void setSourceType( String sourceType ) {
      this.sourceType = sourceType;
  }

  public String getLocale() {
      return locale;
  }

  public void setLocale( String locale ) {
      this.locale = locale;
  }

  public String getTitle() {
      return title;
  }

  public void setTitle( String title ) {
      this.title = title;
  }

  public String getBody() {
      return body;
  }

  public void setBody( String body ) {
      this.body = body;
  }

  public boolean isOutdated() {
      return outdated;
  }

  public void setOutdated( boolean outdated ) {
      this.outdated = outdated;
  }

  public boolean isDraft() {
      return draft;
  }

  public void setDraft( boolean draft ) {
      this.draft = draft;
  }

  public Date getCreatedAt() {
      return createdAt;
  }

  public void setCreatedAt( Date createdAt ) {
      this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
      return updatedAt;
  }

  public void setUpdatedAt( Date updatedAt ) {
      this.updatedAt = updatedAt;
  }

  public Long getUpdatedById() {
      return updatedById;
  }

  public void setUpdatedById( Long updatedById ) {
      this.updatedById = updatedById;
  }

  public Long getCreatedById() {
      return createdById;
  }

  public void setCreatedById( Long createdById ) {
      this.createdById = createdById;
  }
  @Override
  public String toString() {
      return "Translation{" +
          "id=" + id + '\'' +
          ", url=" + url + '\'' +
          ", htmlUrl=" + htmlUrl + '\'' +
          ", sourceId=" + sourceId + '\'' +
          ", sourceType=" + sourceType + '\'' +
          ", locale=" + locale + '\'' +
          ", title=" + title + '\'' +
          ", body=" + body + '\'' +
          ", outdated=" + outdated + '\'' +
          ", draft=" + draft + '\'' +
          ", createdAt=" + createdAt + '\'' +
          ", updatedAt=" + updatedAt + '\'' +
          ", updatedById=" + updatedById + '\'' +
          ", createdById=" + createdById + '\'' +
          '}';
  }
}
