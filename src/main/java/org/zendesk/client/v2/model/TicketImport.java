package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * @author robert-fernandes
 */
@JsonIgnoreProperties(value = "comment", ignoreUnknown = true)
public class TicketImport extends Ticket {

    private static final long serialVersionUID = 1L;

    private List<Comment> comments;
    private Date solvedAt;

    public TicketImport() {
    }

    public TicketImport(Requester requester, String subject, List<Comment> comments) {
        super(requester, subject, null);
        this.comments = comments;
    }

    public TicketImport(long requesterId, String subject, List<Comment> comments) {
        super(requesterId, subject, null);
        this.comments = comments;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @JsonProperty("solved_at")
    public Date getSolvedAt() {
        return solvedAt;
    }

    public void setSolvedAt(Date solvedAt) {
        this.solvedAt = solvedAt;
    }

    @Override
    public Comment getComment() {
        return null;
    }

    @Override
    public void setComment(Comment comment) {
        String msg = "single comment is not supported for ticket import, include in comments list instead";
        throw new UnsupportedOperationException(msg);
    }

    @Override
    public String toString() {
        return "Ticket" +
                "{assigneeId=" + getAssigneeId() +
                ", id=" + getId() +
                ", url='" + getUrl() + '\'' +
                ", externalId='" + getExternalId() + '\'' +
                ", type='" + getType() + '\'' +
                ", subject='" + getSubject() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", priority='" + getPriority() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", recipient='" + getRecipient() + '\'' +
                ", requesterId=" + getRequesterId() +
                ", submitterId=" + getSubmitterId() +
                ", organizationId=" + getOrganizationId() +
                ", groupId=" + getGroupId() +
                ", collaboratorIds=" + getCollaboratorIds() +
                ", forumTopicId=" + getForumTopicId() +
                ", problemId=" + getProblemId() +
                ", hasIncidents=" + isHasIncidents() +
                ", dueAt=" + getDueAt() +
                ", tags=" + getTags() +
                ", via=" + getVia() +
                ", customFields=" + getCustomFields() +
                ", satisfactionRating=" + getSatisfactionRating() +
                ", sharingAgreementIds=" + getSharingAgreementIds() +
                ", followupIds=" + getFollowupIds() +
                ", ticketFormId=" + getTicketFormId() +
                ", brandId=" + getBrandId() +
                ", solvedAt=" + getSolvedAt() +
                ", updatedAt=" + getUpdatedAt() +
                ", solvedAt=" + solvedAt +
                ", comments=" + comments +
                '}';
    }
}
