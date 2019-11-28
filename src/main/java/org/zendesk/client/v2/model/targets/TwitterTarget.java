package org.zendesk.client.v2.model.targets;

/**
 * @author adavidson
 */
public class TwitterTarget extends Target {
   private String token;
   private String secret;

   @Override
   public String getType() {
      return "twitter_target";
   }

   @Override
   public String toString() {
      return "UrlTarget" +
              "{id=" + getId() +
              ", title=" + getTitle() +
              ", type=" + getType() +
              ", active=" + isActive() +
              ", createdAt=" + getCreatedAt() +
              ", token=" + token +
              ", secret=" + secret +
              '}';
   }

   public String getToken() {
      return token;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public String getSecret() {
      return secret;
   }

   public void setSecret(String secret) {
      this.secret = secret;
   }
 

}
