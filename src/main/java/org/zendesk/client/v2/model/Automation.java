package org.zendesk.client.v2.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * https://developer.zendesk.com/rest_api/docs/core/automations
 * 
 * @author Sandeep Kaul (sandeep.kaul@olacabs.com)
 *
 */
public class Automation implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private String title;
  private Boolean active;
  private List<Action> actions;
  private Conditions conditions;
  private int position;
  private Date createdAt;
  private Date updatedAt;
  
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
  public Boolean getActive() {
    return active;
  }
  public void setActive(Boolean active) {
    this.active = active;
  }
  public List<Action> getActions() {
    return actions;
  }
  public void setActions(List<Action> actions) {
    this.actions = actions;
  }
  public Conditions getConditions() {
    return conditions;
  }
  public void setConditions(Conditions conditions) {
    this.conditions = conditions;
  }
  public Date getCreatedAt() {
    return createdAt;
  }
  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }
  public Date getUpdatedAt() {
    return updatedAt;
  }
  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }
  public int getPosition() {
    return position;
  }
  public void setPosition(int position) {
    this.position = position;
  }
  @Override
  public String toString() {
    return "Automation [id=" + id + ", title=" + title + ", active=" + active + ", actions="
        + actions + ", conditions=" + conditions + ", position=" + position + ", createdAt="
        + createdAt + ", updatedAt=" + updatedAt + "]";
  }
  
  
  
}
