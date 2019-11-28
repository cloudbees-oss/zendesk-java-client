package org.zendesk.client.v2.model.targets;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author adavidson
 */
public class CampfireTarget extends Target {
   private String  subdomain;
   private boolean ssl;
   private boolean preserveFormat;
   private String  token;
   private String  room;

   @Override
   public String getType() {
      return "campfire_target";
   }

   @Override
   public String toString() {
      return "UrlTarget" +
              "{id=" + getId() +
              ", title=" + getTitle() +
              ", type=" + getType() +
              ", active=" + isActive() +
              ", createdAt=" + getCreatedAt() +
              ", subdomain=" + subdomain +
              ", token=" + token +
              ", ssl=" + ssl +
              ", preserveFormat=" + preserveFormat +
              ", token=" + token +
              ", room=" + room +
              '}';
   }

   public String getSubdomain() {
      return subdomain;
   }

   public void setSubdomain(String subdomain) {
      this.subdomain = subdomain;
   }

   public boolean isSsl() {
      return ssl;
   }

   public void setSsl(boolean ssl) {
      this.ssl = ssl;
   }

   @JsonProperty("preserve_format")
   public boolean isPreserveFormat() {
      return preserveFormat;
   }

   public void setPreserveFormat(boolean preserveFormat) {
      this.preserveFormat = preserveFormat;
   }

   public String getToken() {
      return token;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public String getRoom() {
      return room;
   }

   public void setRoom(String room) {
      this.room = room;
   }

}
