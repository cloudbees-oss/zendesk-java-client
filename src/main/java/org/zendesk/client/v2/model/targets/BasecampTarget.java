package org.zendesk.client.v2.model.targets;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author adavidson
 */
public class BasecampTarget extends Target {
   private String targetUrl;
   private String token;
   private String projectId;
   private String username;
   private String password;
   private String resource;
   private String messageId;
   private String todoListId;

   @Override
   public String getType() {
      return "basecamp_target";
   }

   @JsonProperty("project_id")
   public String getProjectId() {
      return projectId;
   }

   public void setProjectId(String projectId) {
      this.projectId = projectId;
   }

   @JsonProperty("message_id")
   public String getMessageId() {
      return messageId;
   }

   public void setMessageId(String messageId) {
      this.messageId = messageId;
   }
   
   @JsonProperty("todo_list_id")
   public String getTodoListId() {
      return todoListId;
   }

   public void setTodoListId(String todoListId) {
      this.todoListId = todoListId;
   }

   @JsonProperty("target_url")
   public String getTargetUrl() {
      return targetUrl;
   }

   public void setTargetUrl(String targetUrl) {
      this.targetUrl = targetUrl;
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

   public String getToken() {
      return token;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public String getResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
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
      sb.append(", targetUrl=").append(targetUrl);
      sb.append(", token=").append(token);
      sb.append(", projectId=").append(projectId);
      sb.append(", resource=").append(resource);
      sb.append(", messageId=").append(messageId);
      sb.append(", todoListId=").append(todoListId);
      sb.append(", username=").append(username);
      sb.append(", password=").append(password);
      sb.append('}');
      return sb.toString();
   }

}
