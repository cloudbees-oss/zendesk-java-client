package org.zendesk.client.v2.model.hc;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.zendesk.client.v2.model.SearchResultEntity;

import java.util.Date;
import java.util.List;

/**
 * User Segments
 * A user segment defines who can view the content of a section or topic.
 * https://developer.zendesk.com/rest_api/docs/help_center/user_segments
 * @author Maxim Savenko (maxim.savenko@gmail.com)
 */
public class UserSegment implements SearchResultEntity {
    /** Automatically assigned when the user segment is created */
    private Long id;
    /** User segment name (localized to the locale of the current user for built-in user segments) */
    private String name;
    /** The set of users who can view content */
    @JsonProperty("user_type")
    private UserType userType;
    /** The ids of user segments that have edit privileges */
    @JsonProperty("group_ids")
    private List<Long> groupIds;
    /** The ids of the organizations that have access */
    @JsonProperty("organization_ids")
    private List<Long> organizationIds;
    /** All the tags a user must have to have access */
    private List<String> tags;
    /** A user must have at least one tag in the list to have access */
    @JsonProperty("or_tags")
    private List<String> orTags;
    /** When the user segment was created */
    @JsonProperty("created_at")
    private Date createdAt;
    /** When the user segment was last updated */
    @JsonProperty("updated_at")
    private Date updatedAt;
    /** Whether the user segment is built-in. Built-in user segments cannot be modified */
    @JsonProperty("built_in")
    private Boolean builtIn;

    public Long getId() {
        return id;
    }

    public UserSegment setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserSegment setName(String name) {
        this.name = name;
        return this;
    }

    public UserType getUserType() {
        return userType;
    }

    public UserSegment setUserType(UserType userType) {
        this.userType = userType;
        return this;
    }

    public List<Long> getGroupIds() {
        return groupIds;
    }

    public UserSegment setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
        return this;
    }

    public List<Long> getOrganizationIds() {
        return organizationIds;
    }

    public UserSegment setOrganizationIds(List<Long> organizationIds) {
        this.organizationIds = organizationIds;
        return this;
    }

    public List<String> getTags() {
        return tags;
    }

    public UserSegment setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public List<String> getOrTags() {
        return orTags;
    }

    public UserSegment setOrTags(List<String> orTags) {
        this.orTags = orTags;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public UserSegment setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public UserSegment setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Boolean getBuiltIn() {
        return builtIn;
    }

    public UserSegment setBuiltIn(Boolean builtIn) {
        this.builtIn = builtIn;
        return this;
    }

    @Override
    public String toString() {
        return "UserSegment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userType='" + userType + '\'' +
                ", groupIds=" + groupIds +
                ", organizationIds=" + organizationIds +
                ", tags=" + tags +
                ", orTags=" + orTags +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", builtIn=" + builtIn +
                '}';
    }
}
