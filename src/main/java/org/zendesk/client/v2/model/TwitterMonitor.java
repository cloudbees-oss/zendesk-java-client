package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

public class TwitterMonitor implements SearchResultEntity, Serializable {

    private static final long serialVersionUID = 1L;

   private Long    id;
   private String  screenName;
   private Long    twitterUserId;
   private Date    createdAt;
   private Date    updatedAt;
   private String  avatarUrl;
   private String  name;
   private boolean allowReply;

   @JsonProperty("avatar_url")
   public String getAvatarUrl() {
      return avatarUrl;
   }

   public void setAvatarUrl(String avatarUrl) {
      this.avatarUrl = avatarUrl;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @JsonProperty("allow_reply")
   public boolean isAllowReply() {
      return allowReply;
   }

   public void setAllowReplay(boolean allowReplay) {
      this.allowReply = allowReplay;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   @JsonProperty("twitter_user_id")
   public Long getTwitterUserId() {
      return twitterUserId;
   }

   public void setTwitterUserId(Long twitterUserId) {
      this.twitterUserId = twitterUserId;
   }

   @JsonProperty("created_at")
   public Date getCreatedAt() {
      return createdAt;
   }

   public void setCreatedAt(Date createdAt) {
      this.createdAt = createdAt;
   }

   @JsonProperty("updated_at")
   public Date getUpdatedAt() {
      return updatedAt;
   }

   public void setUpdatedAt(Date updatedAt) {
      this.updatedAt = updatedAt;
   }

   @JsonProperty("screen_name")
   public String getScreenName() {
      return screenName;
   }

   public void setScreenName(String screenName) {
      this.screenName = screenName;
   }

   @Override
   public String toString() {
      return "TwitterMonitor" +
              "{screenName='" + screenName + '\'' +
              ", id=" + id +
              ", twitterUserId='" + twitterUserId + '\'' +
              ", createdAt='" + createdAt + '\'' +
              ", updatedAt='" + updatedAt + '\'' +
              ", avatarUrl='" + avatarUrl + '\'' +
              ", name='" + name + '\'' +
              ", allowReply=" + allowReply +
              '}';
   }

}
