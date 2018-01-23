package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Forum implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String url;
    private String name;
    private String description;
    private Long categoryId;
    private Long organizationId;
    private Long localeId;
    private Boolean locked;
    private Long unansweredTopics;
    private Long position;
    private TopicType forumType;
    private Access access;
    private List<String> tags;
    private Date createdAt;
    private Date updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @JsonProperty("category_id")
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(final Long categoryId) {
        this.categoryId = categoryId;
    }

    @JsonProperty("organization_id")
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(final Long organizationId) {
        this.organizationId = organizationId;
    }

    @JsonProperty("locale_id")
    public Long getLocaleId() {
        return localeId;
    }

    public void setLocaleId(final Long localeId) {
        this.localeId = localeId;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(final Boolean locked) {
        this.locked = locked;
    }

    @JsonProperty("unanswered_topics")
    public Long getUnansweredTopics() {
        return unansweredTopics;
    }

    public void setUnansweredTopics(final Long unansweredTopics) {
        this.unansweredTopics = unansweredTopics;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(final Long position) {
        this.position = position;
    }

    @JsonProperty("forum_type")
    public TopicType getForumType() {
        return forumType;
    }

    public void setForumType(final TopicType forumType) {
        this.forumType = forumType;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(final Access access) {
        this.access = access;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(final List<String> tags) {
        this.tags = tags;
    }

    @JsonProperty("created_at")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("updated_at")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public enum Access {
        EVERYBODY("everybody"),
        LOGGED_IN("logged-in users"),
        AGENTS_ONLY("agents only");

        private final String name;

        Access(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
