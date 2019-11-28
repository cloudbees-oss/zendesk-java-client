package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.zendesk.client.v2.model.Ticket.Requester;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author stephenc
 * @since 09/04/2013 15:08
 */
public class Request implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Long id;
    protected String url;
    protected String subject;
    protected String description;
    protected Status status;
    protected Ticket.Requester requester;
    protected Long requesterId;
    protected Long organizationId;
    protected Via via;
    protected Long viaFollowupSourceId;
    protected Date createdAt;
    protected Date updatedAt;
    protected Comment comment;
    protected Boolean solved;
    protected Priority priority;
    protected List<CustomFieldValue> customFields;
    protected Type type;

    @JsonProperty("created_at")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty()
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("organization_id")
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    @JsonProperty("requester_id")
    public Long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
        if (requesterId != null) {
            this.requester = null;
        }
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @JsonProperty("updated_at")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Via getVia() {
        return via;
    }

    public void setVia(Via via) {
        this.via = via;
    }

    @JsonProperty("via_followup_source_id")
    public Long getViaFollowupSourceId() {
        return viaFollowupSourceId;
    }

    public void setViaFollowupSourceId(Long viaFollowupSourceId) {
        this.viaFollowupSourceId = viaFollowupSourceId;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    @JsonProperty("solved")
    public Boolean getSolved() {
        return solved;
    }

    public void setSolved(Boolean solved) {
        this.solved = solved;
    }

    @JsonProperty("priority")
    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @JsonProperty("custom_fields")
    public List<CustomFieldValue> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<CustomFieldValue> customFields) {
        this.customFields = customFields;
    }

    public Requester getRequester() {
        return requester;
    }

    public void setRequester(Requester requester) {
        this.requester = requester;
        if (requester != null) {
            this.requesterId = null;
        }
    }

    @JsonProperty("type")
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
