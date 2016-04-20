package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author stephenc
 * @since 22/04/2013 15:38
 */
public class Organization implements SearchResultEntity, Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String externalId;
    private String name;
    private Date createdAt;
    private Date updatedAt;
    private List<String> domainNames;
    private String details;
    private String notes;
    private Long groupId;
    private Boolean sharedTickets;
    private Boolean sharedComments;
    private List<String> tags;
    private Map<String, Object> organizationFields;

    public Organization() {
    }

    @JsonProperty("created_at")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @JsonProperty("domain_names")
    public List<String> getDomainNames() {
        return domainNames;
    }

    public void setDomainNames(List<String> domainNames) {
        this.domainNames = domainNames;
    }

    @JsonProperty("external_id")
    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @JsonProperty("group_id")
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @JsonProperty("shared_comments")
    public Boolean getSharedComments() {
        return sharedComments;
    }

    public void setSharedComments(Boolean sharedComments) {
        this.sharedComments = sharedComments;
    }

    @JsonProperty("shared_tickets")
    public Boolean getSharedTickets() {
        return sharedTickets;
    }

    public void setSharedTickets(Boolean sharedTickets) {
        this.sharedTickets = sharedTickets;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @JsonProperty("updated_at")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("organization_fields")
    public Map<String, Object> getOrganizationFields() {
        return organizationFields;
    }

    public void setOrganizationFields(Map<String, Object> organizationFields) {
        this.organizationFields = organizationFields;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Organization{");
        sb.append("id=").append(id);
        sb.append(", externalId='").append(externalId).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", domainNames=").append(domainNames);
        sb.append(", details='").append(details).append('\'');
        sb.append(", notes='").append(notes).append('\'');
        sb.append(", groupId=").append(groupId);
        sb.append(", sharedTickets=").append(sharedTickets);
        sb.append(", sharedComments=").append(sharedComments);
        sb.append(", tags=").append(tags);
        sb.append(", organizationFields=").append(organizationFields);
        sb.append('}');
        return sb.toString();
    }
}
