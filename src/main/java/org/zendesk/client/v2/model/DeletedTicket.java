package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeletedTicket implements Serializable {
    private static final long serialVersionUID = -1245555299753747844L;

    protected Long id;
    protected String subject;
    protected String description;
    protected Actor actor;
    protected Status previousState;
    protected Date deletedAt;

    public DeletedTicket() {
    }

    public DeletedTicket(Long id, String subject, String description, Actor actor,
                         Status previousState, Date deletedAt) {
        this.id = id;
        this.subject = subject;
        this.description = description;
        this.actor = actor;
        this.previousState = previousState;
        this.deletedAt = deletedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    @JsonProperty("previous_state")
    public Status getPreviousState() {
        return previousState;
    }

    public void setPreviousState(Status previousState) {
        this.previousState = previousState;
    }

    @JsonProperty("deleted_at")
    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString() {
        return "DeletedTicket{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", actor=" + actor +
                ", previousState=" + previousState +
                ", deletedAt=" + deletedAt +
                '}';
    }

    public static class Actor implements Serializable {
        private static final long serialVersionUID = 6945229807147073769L;
        private Long id;
        private String name;

        public Actor() {
        }

        public Actor(Long id) {
            this.id = id;
        }

        public Actor(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Actor" +
                    "{id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
