package org.zendesk.client.v2.model;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
      sb.append("Trigger");
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
      private List<Condition> all = new ArrayList<Condition>();
      private List<Condition> any = new ArrayList<Condition>();

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
         final StringBuilder sb = new StringBuilder();
         sb.append("Conditions");
         sb.append("{all=").append(all);
         sb.append(", any=").append(any);
         sb.append('}');
         return sb.toString();
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
         final StringBuilder sb = new StringBuilder();
         sb.append("Condition");
         sb.append("{field=").append(field);
         sb.append(", operator=").append(operator);
         sb.append(", value=").append(value);
         sb.append('}');
         return sb.toString();
      }

   }

   @JsonDeserialize(using = org.zendesk.client.v2.model.Trigger.Action.ActionDeserializer.class)
   public static class Action {
      private String       field;
      private List<String> value;

      public Action() {}

      public Action(String field, String... values) {
         this.field = field;
         this.value = Arrays.asList(values);
      }

      public String getField() {
         return field;
      }

      public void setField(String field) {
         this.field = field;
      }

      public List<String> getValue() {
         return value;
      }

      public void setValue(List<String> value) {
         this.value = value;
      }

      @Override
      public String toString() {
         final StringBuilder sb = new StringBuilder();
         sb.append("Action");
         sb.append("{field=").append(field);
         sb.append(", value=").append(value);
         sb.append('}');
         return sb.toString();
      }

      // Zendesk sometimes returns JSON with a String instead of String[] so we need a custom deserializer
      public static class ActionDeserializer extends JsonDeserializer<Action> {
         @Override
         public Action deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = jp.getCodec().readTree(jp);
            Action a = new Action();
            a.field = node.get("field").asText();
            a.value = new ArrayList<String>();

            JsonNode n = node.get("value");
            if (n.isArray()) {
               for (final JsonNode objNode : n) {
                  a.value.add(objNode.asText());
               }
            } else {
               a.value.add(node.get("value").asText());
            }
            return a;
         }
      }

   }
}
