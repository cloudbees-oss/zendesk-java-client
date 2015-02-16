package org.zendesk.client.v2.model.targets;

import java.util.Date;

/**
 * https://developer.zendesk.com/rest_api/docs/core/targets 
 * 
 * @author adavidson
 */
public class Target {
   private Long    id;
   private String  title;
   private String  type;
   private boolean active;
   private Date    createdAt;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public boolean isActive() {
      return active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public Date getCreatedAt() {
      return createdAt;
   }

   public void setCreatedAt(Date createdAt) {
      this.createdAt = createdAt;
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("Target");
      sb.append("{id=").append(id);
      sb.append(", title=").append(title);
      sb.append(", type=").append(type);
      sb.append(", active=").append(active);
      sb.append(", createdAt=").append(createdAt);
      sb.append('}');
      return sb.toString();
   }

}
