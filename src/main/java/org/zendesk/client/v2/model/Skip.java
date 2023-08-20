package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.Objects;

public class Skip {
  private Long id;

  @JsonProperty("ticket_id")
  private Long ticketId;

  @JsonProperty("user_id")
  private Long userId;

  private String reason;

  @JsonProperty("created_at")
  private Date createdAt;

  @JsonProperty("updated_at")
  private Date updatedAt;

  private Ticket ticket;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTicketId() {
    return ticketId;
  }

  public void setTicketId(Long ticketId) {
    this.ticketId = ticketId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
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

  public Ticket getTicket() {
    return ticket;
  }

  public void setTicket(Ticket ticket) {
    this.ticket = ticket;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Skip skip = (Skip) o;
    return Objects.equals(id, skip.id)
        && Objects.equals(ticketId, skip.ticketId)
        && Objects.equals(userId, skip.userId)
        && Objects.equals(reason, skip.reason)
        && Objects.equals(createdAt, skip.createdAt)
        && Objects.equals(updatedAt, skip.updatedAt)
        && Objects.equals(ticket, skip.ticket);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, ticketId, userId, reason, createdAt, updatedAt, ticket);
  }

  @Override
  public String toString() {
    return "Skip{"
        + "id="
        + id
        + ", ticketId="
        + ticketId
        + ", userId="
        + userId
        + ", reason='"
        + reason
        + '\''
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + ", ticket="
        + ticket
        + '}';
  }
}
