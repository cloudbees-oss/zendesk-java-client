package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * User: Dominic (Dominic.Gunn@sulake.com)
 * Date: 17/12/13
 * Time: 12:43
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Macro implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private String title;
    private boolean active;
    private List<Action> actions;
    private Date createdAt;
    private Date updatedAt;

    public Macro() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    @JsonProperty("created_at")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("deleted_at")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Macro{" + "id=" + id +
                ", title='" + title + '\'' +
                ", active='" + active + '\'' +
                ", actions=" + actions +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
