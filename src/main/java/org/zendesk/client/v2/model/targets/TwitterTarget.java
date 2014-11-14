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
      final StringBuilder sb = new StringBuilder();
      sb.append("UrlTarget");
      sb.append("{id=").append(getId());
      sb.append(", title=").append(getTitle());
      sb.append(", type=").append(getType());
      sb.append(", active=").append(isActive());
      sb.append(", createdAt=").append(getCreatedAt());
      sb.append(", token=").append(token);
      sb.append(", secret=").append(secret);
      sb.append('}');
      return sb.toString();
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
