package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;

/** https://developer.zendesk.com/api-reference/ticketing/tickets/custom_ticket_statuses/ */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomTicketStatus implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private boolean active;
  private String description;

  @JsonProperty("agent_label")
  private String agentLabel;

  @JsonProperty("created_at")
  private Date createdAt;

  @JsonProperty("end_user_description")
  private String endUserDescription;

  @JsonProperty("end_user_label")
  private String endUserLabel;

  @JsonProperty("raw_agent_label")
  private String rawAgentLabel;

  @JsonProperty("raw_description")
  private String rawDescription;

  @JsonProperty("raw_end_user_description")
  private String rawEndUserDescription;

  @JsonProperty("raw_end_user_label")
  private String rawEndUserLabel;

  @JsonProperty("status_category")
  private String statusCategory;

  @JsonProperty("updated_at")
  private Date updatedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAgentLabel() {
    return agentLabel;
  }

  public void setAgentLabel(String agentLabel) {
    this.agentLabel = agentLabel;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public String getEndUserDescription() {
    return endUserDescription;
  }

  public void setEndUserDescription(String endUserDescription) {
    this.endUserDescription = endUserDescription;
  }

  public String getEndUserLabel() {
    return endUserLabel;
  }

  public void setEndUserLabel(String endUserLabel) {
    this.endUserLabel = endUserLabel;
  }

  public String getRawAgentLabel() {
    return rawAgentLabel;
  }

  public void setRawAgentLabel(String rawAgentLabel) {
    this.rawAgentLabel = rawAgentLabel;
  }

  public String getRawDescription() {
    return rawDescription;
  }

  public void setRawDescription(String rawDescription) {
    this.rawDescription = rawDescription;
  }

  public String getRawEndUserDescription() {
    return rawEndUserDescription;
  }

  public void setRawEndUserDescription(String rawEndUserDescription) {
    this.rawEndUserDescription = rawEndUserDescription;
  }

  public String getRawEndUserLabel() {
    return rawEndUserLabel;
  }

  public void setRawEndUserLabel(String rawEndUserLabel) {
    this.rawEndUserLabel = rawEndUserLabel;
  }

  public String getStatusCategory() {
    return statusCategory;
  }

  public void setStatusCategory(String statusCategory) {
    this.statusCategory = statusCategory;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public String toString() {
    return "CustomTicketStatus{"
        + "id="
        + id
        + ", active="
        + active
        + ", description='"
        + description
        + '\''
        + ", agentLabel='"
        + agentLabel
        + '\''
        + ", createdAt="
        + createdAt
        + ", endUserDescription='"
        + endUserDescription
        + '\''
        + ", endUserLabel='"
        + endUserLabel
        + '\''
        + ", rawAgentLabel='"
        + rawAgentLabel
        + '\''
        + ", rawDescription='"
        + rawDescription
        + '\''
        + ", rawEndUserDescription='"
        + rawEndUserDescription
        + '\''
        + ", rawEndUserLabel='"
        + rawEndUserLabel
        + '\''
        + ", statusCategory='"
        + statusCategory
        + '\''
        + ", updatedAt="
        + updatedAt
        + '}';
  }
}
