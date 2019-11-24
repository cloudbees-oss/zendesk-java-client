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
      return "UrlTarget" +
              "{id=" + getId() +
              ", title=" + getTitle() +
              ", type=" + getType() +
              ", active=" + isActive() +
              ", createdAt=" + getCreatedAt() +
              ", email=" + email +
              ", subject=" + subject +
              '}';
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
