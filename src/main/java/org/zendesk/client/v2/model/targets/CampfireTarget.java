package org.zendesk.client.v2.model.targets;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author adavidson
 */
public class CampfireTarget extends Target {
   private String  subdomain;
   private Boolean ssl;
   private Boolean preserveFormat;
   private String  token;
   private String  room;

   @Override
   public String getType() {
      return "campfire_target";
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("UrlTarget");
      sb.append("{id=").append(getId());
      sb.append(", title=").append(getTitle());
      sb.append(", type=").append(getType());
      sb.append(", active=").append(isActive());
      sb.append(", createdAt=").append(getCreatedAt());
      sb.append(", subdomain=").append(subdomain);
      sb.append(", token=").append(token);
      sb.append(", ssl=").append(ssl);
      sb.append(", preserveFormat=").append(preserveFormat);
      sb.append(", token=").append(token);
      sb.append(", room=").append(room);
      sb.append('}');
      return sb.toString();
   }

   public String getSubdomain() {
      return subdomain;
   }

   public void setSubdomain(String subdomain) {
      this.subdomain = subdomain;
   }

   public Boolean isSsl() {
      return ssl;
   }

   public void setSsl(Boolean ssl) {
      this.ssl = ssl;
   }

   @JsonProperty("preserve_format")
   public Boolean isPreserveFormat() {
      return preserveFormat;
   }

   public void setPreserveFormat(Boolean preserveFormat) {
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
