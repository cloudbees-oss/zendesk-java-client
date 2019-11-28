package org.zendesk.client.v2.model.targets;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author adavidson
 */
public class UrlTarget extends Target {
   private String targetUrl;
   private String method;
   private String attribute;
   private String username;
   private String password;

   @Override
   public String getType() {
      return "url_target";
   }
   
   @JsonProperty("target_url")
   public String getTargetUrl() {
      return targetUrl;
   }

   public void setTargetUrl(String targetUrl) {
      this.targetUrl = targetUrl;
   }

   public String getMethod() {
      return method;
   }

   public void setMethod(String method) {
      this.method = method;
   }

   public String getAttribute() {
      return attribute;
   }

   public void setAttribute(String attribute) {
      this.attribute = attribute;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   @Override
   public String toString() {
      return "UrlTarget" +
              "{id=" + getId() +
              ", title=" + getTitle() +
              ", type=" + getType() +
              ", active=" + isActive() +
              ", createdAt=" + getCreatedAt() +
              ", targetUrl=" + targetUrl +
              ", method=" + method +
              ", attribute=" + attribute +
              ", username=" + username +
              ", password=" + password +
              '}';
   }

}
