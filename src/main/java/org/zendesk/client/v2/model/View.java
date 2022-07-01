package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class View implements Serializable {

    private static final long serialVersionUID = 8162172428393948830L;

    private long id;
    private String title;
    private boolean active;
    private String updatedAt;
    private String createdAt;
    private long position;
    private String description;
    private Conditions conditions;
    private boolean watchable;

    public void setId(long id) {
        this.id = id;
    }

    @JsonProperty("id")
    public long getId() {
        return this.id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("title")
    public String getTitle() {
        return this.title;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @JsonProperty("active")
    public boolean getActive() {
        return this.active;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("updated_at")
    public String getUpdatedAt() {
        return this.updatedAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    @JsonProperty("position")
    public long getPosition() {
        return this.position;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("description")
    public String getDescription() {
        return this.description;
    }

    public void setConditions(Conditions conditions) {
        this.conditions = conditions;
    }

    @JsonProperty("conditions")
    public Conditions getConditions() {
        return this.conditions;
    }

    public void setWatchable(boolean watchable) {
        this.watchable = watchable;
    }

    @JsonProperty("watchable")
    public boolean getWatchable() {
        return this.watchable;
    }

    public String toString() {
        return "View " +
            "{id=" + id +
            ", title=" + title +
            ", active=" + active +
            ", updatedAt=" + updatedAt +
            ", createdAt=" + createdAt +
            ", position=" + position +
            ", description=" + description +
            ", conditions=" + conditions +
            ", watchable=" + watchable +
            "}";
    }
}
