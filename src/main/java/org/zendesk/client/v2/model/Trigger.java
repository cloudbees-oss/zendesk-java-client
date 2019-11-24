package org.zendesk.client.v2.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * https://developer.zendesk.com/rest_api/docs/core/triggers
 * 
 * @author adavidson
 */
public class Trigger implements Serializable {

    private static final long serialVersionUID = 1L;

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
      return "Trigger" +
              "{id=" + id +
              ", title=" + title +
              ", active=" + active +
              ", position=" + position +
              ", active=" + active +
              ", conditions=" + conditions +
              ", actions=" + actions +
              ", updatedAt=" + updatedAt +
              '}';
   }

   public static class Conditions {
      private List<Condition> all = new ArrayList<>();
      private List<Condition> any = new ArrayList<>();

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

      @Override
      public String toString() {
         return "Conditions" +
                 "{all=" + all +
                 ", any=" + any +
                 '}';
      }
   }

   public static class Condition {
      private String field;
      private String operator;
      private String value;

      public Condition() {}

      public Condition(String field, String operator, String value) {
         this.field = field;
         this.operator = operator;
         this.value = value;
      }

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

      @Override
      public String toString() {
         return "Condition" +
                 "{field=" + field +
                 ", operator=" + operator +
                 ", value=" + value +
                 '}';
      }

   }

}
