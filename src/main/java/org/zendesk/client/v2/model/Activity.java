package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class Activity {
  private Long id;

  private User actor;

  @JsonProperty("actor_id")
  private Long actorId;

  private User user;

  @JsonProperty("user_id")
  private Long userId;

  @JsonProperty("created_at")
  private Date createdAt;

  @JsonProperty("updated_at")
  private Date updatedAt;

  private String title;

  private String url;

  private String verb;

  private Ticket target;

  /**
   * Can be a Ticket, Comment, or Change. Needs to be deserialized to the correct type by the
   * client. See <a
   * href="https://developer.zendesk.com/api-reference/ticketing/tickets/activity_stream/#json-format">documentation</a>.
   */
  private Map<String, Object> object;

  public Activity() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getActor() {
    return actor;
  }

  public void setActor(User actor) {
    this.actor = actor;
  }

  public Long getActorId() {
    return actorId;
  }

  public void setActorId(Long actorId) {
    this.actorId = actorId;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getVerb() {
    return verb;
  }

  public void setVerb(String verb) {
    this.verb = verb;
  }

  public Ticket getTarget() {
    return target;
  }

  public void setTarget(Ticket target) {
    this.target = target;
  }

  public Map<String, Object> getObject() {
    return object;
  }

  public void setObject(Map<String, Object> object) {
    this.object = object;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Activity activity = (Activity) o;
    return Objects.equals(id, activity.id)
        && Objects.equals(actor, activity.actor)
        && Objects.equals(actorId, activity.actorId)
        && Objects.equals(user, activity.user)
        && Objects.equals(userId, activity.userId)
        && Objects.equals(createdAt, activity.createdAt)
        && Objects.equals(updatedAt, activity.updatedAt)
        && Objects.equals(title, activity.title)
        && Objects.equals(url, activity.url)
        && Objects.equals(verb, activity.verb)
        && Objects.equals(target, activity.target)
        && Objects.equals(object, activity.object);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id, actor, actorId, user, userId, createdAt, updatedAt, title, url, verb, target, object);
  }

  @Override
  public String toString() {
    return "Activity{"
        + "id="
        + id
        + ", actor="
        + actor
        + ", actorId="
        + actorId
        + ", user="
        + user
        + ", userId="
        + userId
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + ", title='"
        + title
        + '\''
        + ", url='"
        + url
        + '\''
        + ", verb='"
        + verb
        + '\''
        + ", target="
        + target
        + ", object="
        + object
        + '}';
  }
}
