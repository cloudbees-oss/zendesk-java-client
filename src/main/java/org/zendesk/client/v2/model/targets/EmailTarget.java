package org.zendesk.client.v2.model.targets;

/**
 * @author adavidson
 */
public class EmailTarget extends Target {
   private String email;
   private String subject;

   @Override
   public String getType() {
      return "email_target";
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
      sb.append(", email=").append(email);
      sb.append(", subject=").append(subject);
      sb.append('}');
      return sb.toString();
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getSubject() {
      return subject;
   }

   public void setSubject(String subject) {
      this.subject = subject;
   }

}
