package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author stephenc
 * @since 04/04/2013 14:25
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticket {
    private Integer id;
    private String url;
    private String externalId;
    private String type;
    private String subject;
    private String description;
    private Comment comment;
    private String priority;
    private String status;
    private String recipient;
    private Requester requester;
    private Integer requesterId;
    private Integer submitterId;
    private Integer assigneeId;
    private Integer organizationId;
    private Integer groupId;
    private List<Integer> collaboratorIds;
    private Integer forumTopicId;
    private Integer problemId;
    private boolean hasIncidents;
    private Date dueAt;
    private List<String> tags;
    private Via via;
    private List<CustomFieldValue> customFields;
    private SatisfactionRating satisfactionRating;
    private List<Integer> sharingAgreementIds;
    private List<Integer> followupIds;
    private Integer ticketFormId;
    private Date createdAt;
    private Date updatedAt;

    public Ticket() {
    }

    public Ticket(Requester requester, String subject, Comment comment) {
        this.subject = subject;
        this.requester = requester;
        this.comment = comment;
    }

    public Ticket(int requesterId, String subject, Comment comment) {
        this.subject = subject;
        this.requesterId = requesterId;
        this.comment = comment;
    }

    @JsonProperty("assignee_id")
    public Integer getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Integer assigneeId) {
        this.assigneeId = assigneeId;
    }

    @JsonProperty("collaborator_ids")
    public List<Integer> getCollaboratorIds() {
        return collaboratorIds;
    }

    public void setCollaboratorIds(List<Integer> collaboratorIds) {
        this.collaboratorIds = collaboratorIds;
    }

    @JsonProperty("created_at")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("custom_fields")
    public List<CustomFieldValue> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<CustomFieldValue> customFields) {
        this.customFields = customFields;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    @JsonProperty()
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("due_at")
    public Date getDueAt() {
        return dueAt;
    }

    public void setDueAt(Date dueAt) {
        this.dueAt = dueAt;
    }

    @JsonProperty("external_id")
    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @JsonProperty("followup_ids")
    public List<Integer> getFollowupIds() {
        return followupIds;
    }

    public void setFollowupIds(List<Integer> followupIds) {
        this.followupIds = followupIds;
    }

    @JsonProperty("forum_topic_id")
    public Integer getForumTopicId() {
        return forumTopicId;
    }

    public void setForumTopicId(Integer forumTopicId) {
        this.forumTopicId = forumTopicId;
    }

    @JsonProperty("group_id")
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @JsonProperty("has_incidents")
    public boolean isHasIncidents() {
        return hasIncidents;
    }

    public void setHasIncidents(boolean hasIncidents) {
        this.hasIncidents = hasIncidents;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("organization_id")
    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @JsonProperty("problem_id")
    public Integer getProblemId() {
        return problemId;
    }

    public void setProblemId(Integer problemId) {
        this.problemId = problemId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
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

    @JsonProperty("requester_id")
    public Integer getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Integer requesterId) {
        this.requesterId = requesterId;
        if (requesterId != null) {
            this.requester = null;
        }
    }

    @JsonProperty("satisfaction_rating")
    public SatisfactionRating getSatisfactionRating() {
        return satisfactionRating;
    }

    public void setSatisfactionRating(SatisfactionRating satisfactionRating) {
        this.satisfactionRating = satisfactionRating;
    }

    @JsonProperty("sharing_agreement_ids")
    public List<Integer> getSharingAgreementIds() {
        return sharingAgreementIds;
    }

    public void setSharingAgreementIds(List<Integer> sharingAgreementIds) {
        this.sharingAgreementIds = sharingAgreementIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @JsonProperty("submitter_id")
    public Integer getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(Integer submitterId) {
        this.submitterId = submitterId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @JsonProperty("ticket_form_id")
    public Integer getTicketFormId() {
        return ticketFormId;
    }

    public void setTicketFormId(Integer ticketFormId) {
        this.ticketFormId = ticketFormId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Ticket");
        sb.append("{assigneeId=").append(assigneeId);
        sb.append(", id=").append(id);
        sb.append(", url='").append(url).append('\'');
        sb.append(", externalId='").append(externalId).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", subject='").append(subject).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", priority='").append(priority).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", recipient='").append(recipient).append('\'');
        sb.append(", requesterId=").append(requesterId);
        sb.append(", submitterId=").append(submitterId);
        sb.append(", organizationId=").append(organizationId);
        sb.append(", groupId=").append(groupId);
        sb.append(", collaboratorIds=").append(collaboratorIds);
        sb.append(", forumTopicId=").append(forumTopicId);
        sb.append(", problemId=").append(problemId);
        sb.append(", hasIncidents=").append(hasIncidents);
        sb.append(", dueAt=").append(dueAt);
        sb.append(", tags=").append(tags);
        sb.append(", via=").append(via);
        sb.append(", customFields=").append(customFields);
        sb.append(", satisfactionRating=").append(satisfactionRating);
        sb.append(", sharingAgreementIds=").append(sharingAgreementIds);
        sb.append(", followupIds=").append(followupIds);
        sb.append(", ticketFormId=").append(ticketFormId);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append('}');
        return sb.toString();
    }

    public static class Comment {
        private String body;
        private List<String> uploads;

        public Comment() {
        }

        public Comment(String body) {
            this.body = body;
        }

        public Comment(String body, String... uploads) {
            this.body = body;
            this.uploads = uploads.length == 0 ? null : Arrays.asList(uploads);
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public List<String> getUploads() {
            return uploads;
        }

        public void setUploads(List<String> uploads) {
            this.uploads = uploads;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Comment");
            sb.append("{body='").append(body).append('\'');
            sb.append(", uploads=").append(uploads);
            sb.append('}');
            return sb.toString();
        }
    }

    public static class Requester {
        private Integer localeId;
        private String name;
        private String email;

        public Requester() {
        }

        public Requester(String email) {
            this.email = email;
        }

        public Requester(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public Requester(Integer localeId, String name, String email) {
            this.localeId = localeId;
            this.name = name;
            this.email = email;
        }

        @JsonProperty("locale_id")
        public Integer getLocaleId() {
            return localeId;
        }

        public void setLocaleId(Integer localeId) {
            this.localeId = localeId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Requester");
            sb.append("{localeId=").append(localeId);
            sb.append(", name='").append(name).append('\'');
            sb.append(", email='").append(email).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

}
