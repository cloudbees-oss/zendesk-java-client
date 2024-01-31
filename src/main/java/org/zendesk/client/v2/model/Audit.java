package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.zendesk.client.v2.model.events.Event;

/**
 * @author stephenc
 * @since 05/04/2013 10:22
 */
public class Audit implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private Long ticketId;
  private Map<String, Object> metadata;
  private Via via;
  private Date createdAt;
  private Long authorId;
  private List<Event> events;

  @JsonProperty("author_id")
  public Long getAuthorId() {
    return authorId;
  }

  public void setAuthorId(Long authorId) {
    this.authorId = authorId;
  }

  @JsonProperty("created_at")
  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public List<Event> getEvents() {
    return events;
  }

  public void setEvents(List<Event> events) {
    this.events = events;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  @JsonProperty("ticket_id")
  public Long getTicketId() {
    return ticketId;
  }

  public void setTicketId(Long ticketId) {
    this.ticketId = ticketId;
  }

  public Via getVia() {
    return via;
  }

  public void setVia(Via via) {
    this.via = via;
  }

  @Override
  public String toString() {
    return "Audit"
        + "{authorId="
        + authorId
        + ", id="
        + id
        + ", ticketId="
        + ticketId
        + ", metadata="
        + metadata
        + ", via="
        + via
        + ", createdAt="
        + createdAt
        + ", events="
        + events
        + '}';
  }
}
