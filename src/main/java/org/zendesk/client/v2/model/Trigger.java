package org.zendesk.client.v2.model;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * https://developer.zendesk.com/rest_api/docs/core/triggers
 * 
 * @author adavidson
 */
public class Trigger {
   private Long         id;
   private String       title;
   private boolean      active;
   private int          position;
   private Conditions   conditions;
   private List<Action> actions;
   private Date         createdAt;
   private Date         updatedAt;

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

   public int getPosition() {
      return position;
   }

   public void setPosition(int position) {
      this.position = position;
   }

   public Conditions getConditions() {
      return conditions;
   }

   public void setConditions(Conditions conditions) {
      this.conditions = conditions;
   }

   public List<Action> getActions() {
      return actions;
   }

   public void setActions(List<Action> actions) {
      this.actions = actions;
   }

   public Date getUpdatedAt() {
      return updatedAt;
   }

   public void setUpdatedAt(Date updatedAt) {
      this.updatedAt = updatedAt;
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("Target");
      sb.append("{id=").append(id);
      sb.append(", title=").append(title);
      sb.append(", active=").append(active);
      sb.append(", position=").append(position);
      sb.append(", active=").append(active);
      sb.append(", conditions=").append(conditions);
      sb.append(", actions=").append(actions);
      sb.append(", updatedAt=").append(updatedAt);
      sb.append('}');
      return sb.toString();
   }

   public static class Conditions {
      private List<Condition> all;
      private List<Condition> any;

      public List<Condition> getAll() {
         return all;
      }

      public void setAll(List<Condition> all) {
         this.all = all;
      }

      public List<Condition> getAny() {
         return any;
      }

      public void setAny(List<Condition> any) {
         this.any = any;
      }
   }

   public static class Condition {
      private String field;
      private String operator;

      public String getField() {
         return field;
      }

      public void setField(String field) {
         this.field = field;
      }

      public String getOperator() {
         return operator;
      }

      public void setOperator(String operator) {
         this.operator = operator;
      }

      public String getValue() {
         return value;
      }

      public void setValue(String value) {
         this.value = value;
      }

      private String value;
   }

   public static class Action {
      private String   field;
      
      // FIXME: Zendesk sometimes returns JSON with a String instead of String[]
      private String[] value; 

      public String getField() {
         return field;
      }

      public void setField(String field) {
         this.field = field;
      }

      public String[] getValue() {
         return value;
      }

      public void setValue(String[] value) {
         this.value = value;
      }

   }
}
