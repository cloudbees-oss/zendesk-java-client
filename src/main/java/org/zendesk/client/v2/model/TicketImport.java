package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * @author robert-fernandes
 */
@JsonIgnoreProperties(value = "comment", ignoreUnknown = true)
public class TicketImport extends Ticket {

    private static final long serialVersionUID = 1L;

    private List<Comment> comments;

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

    @Override
    public Comment getComment() {
        return null;
    }

    @Override
    public void setComment(Comment comment) {
        throw new UnsupportedOperationException("single comment is not supported for ticket import, include in comments list instead");
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
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", comments=").append(comments);
        sb.append('}');
        return sb.toString();
    }
}
