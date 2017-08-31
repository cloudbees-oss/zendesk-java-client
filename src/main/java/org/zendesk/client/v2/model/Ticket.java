package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * @author stephenc
 * @since 04/04/2013 14:25
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticket extends Request implements SearchResultEntity {

    private static final long serialVersionUID = 1L;

    private String externalId;
    private Type type;
    private Priority priority;
    private String recipient;
    private Long submitterId;
    private Long assigneeId;
    private Long groupId;
    private List<Long> collaboratorIds;
    private List<Collaborator> collaborators;
    private Long forumTopicId;
    private Long problemId;
    private boolean hasIncidents;
    private Date dueAt;
    private List<String> tags;
    private List<CustomFieldValue> customFields;
    private SatisfactionRating satisfactionRating;
    private List<Long> sharingAgreementIds;
    private List<Long> followupIds;
    private Long ticketFormId;
    private Long brandId;
    private Boolean isPublic;

    public Ticket() {
    }

    public Ticket(Requester requester, String subject, Comment comment) {
        this.subject = subject;
        this.requester = requester;
        this.comment = comment;
    }

    public Ticket(long requesterId, String subject, Comment comment) {
        this.subject = subject;
        this.requesterId = requesterId;
        this.comment = comment;
    }

    @JsonProperty("assignee_id")
    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    @JsonProperty("collaborator_ids")
    public List<Long> getCollaboratorIds() {
        return collaboratorIds;
    }

    public void setCollaboratorIds(List<Long> collaboratorIds) {
        this.collaboratorIds = collaboratorIds;
    }

    @JsonProperty("collaborators")
    private List<Collaborator> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<Collaborator> collaborators) {
        this.collaborators = collaborators;
    }

    @JsonProperty("custom_fields")
    public List<CustomFieldValue> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<CustomFieldValue> customFields) {
        this.customFields = customFields;
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
    public List<Long> getFollowupIds() {
        return followupIds;
    }

    public void setFollowupIds(List<Long> followupIds) {
        this.followupIds = followupIds;
    }

    @JsonProperty("forum_topic_id")
    public Long getForumTopicId() {
        return forumTopicId;
    }

    public void setForumTopicId(Long forumTopicId) {
        this.forumTopicId = forumTopicId;
    }

    @JsonProperty("group_id")
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @JsonProperty("has_incidents")
    public boolean isHasIncidents() {
        return hasIncidents;
    }

    public void setHasIncidents(boolean hasIncidents) {
        this.hasIncidents = hasIncidents;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @JsonProperty("problem_id")
    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    @JsonProperty("brand_id")
    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
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

    @JsonProperty("satisfaction_rating")
    public SatisfactionRating getSatisfactionRating() {
        return satisfactionRating;
    }

    public void setSatisfactionRating(SatisfactionRating satisfactionRating) {
        this.satisfactionRating = satisfactionRating;
    }

    @JsonProperty("sharing_agreement_ids")
    public List<Long> getSharingAgreementIds() {
        return sharingAgreementIds;
    }

    public void setSharingAgreementIds(List<Long> sharingAgreementIds) {
        this.sharingAgreementIds = sharingAgreementIds;
    }

    @JsonProperty("submitter_id")
    public Long getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(Long submitterId) {
        this.submitterId = submitterId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @JsonProperty("ticket_form_id")
    public Long getTicketFormId() {
        return ticketFormId;
    }

    public void setTicketFormId(Long ticketFormId) {
        this.ticketFormId = ticketFormId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @JsonProperty("is_public")
    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
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
        sb.append(", brandId=").append(brandId);
        sb.append(", isPublic=").append(isPublic);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append('}');
        return sb.toString();
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
