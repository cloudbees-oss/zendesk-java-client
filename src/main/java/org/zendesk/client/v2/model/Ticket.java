package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

/**
 * @author stephenc
 * @since 04/04/2013 14:25
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticket extends Request implements SearchResultEntity {

  private static final long serialVersionUID = -7559199410302237012L;

  private String externalId;
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
  private SatisfactionRating satisfactionRating;
  private List<Long> sharingAgreementIds;
  private List<Long> followupIds;
  private Long ticketFormId;
  private Long brandId;
  private Boolean isPublic;
  private Boolean safeUpdate;

  public Ticket() {}

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

  @JsonProperty("is_public")
  public Boolean getIsPublic() {
    return isPublic;
  }

  public void setIsPublic(Boolean isPublic) {
    this.isPublic = isPublic;
  }

  /**
   * The safe_update &amp; update_stamp parameters are used by the Zendesk API to protect against
   * update collisions. If the safe_update parameter is set to true and Zendesk detects that a
   * ticket has been updated since the time specified in update_stamp then it will return an HTTP
   * CONFLICT status to let the caller know that the ticket was not successfully updated.
   *
   * <p>These properties are annotated with JsonInclude(Include.NON_DEFAULT) so that they will only
   * be serialized to JSON if safeUpdate is set to TRUE.
   *
   * <p>For more information see Zendesk documentation at:
   * https://developer.zendesk.com/documentation/ticketing/managing-tickets/creating-and-updating-tickets/#protecting-against-ticket-update-collisions
   */
  @JsonInclude(Include.NON_DEFAULT)
  @JsonProperty("safe_update")
  public Boolean getSafeUpdate() {
    return safeUpdate;
  }

  public void setSafeUpdate(Boolean safeUpdate) {
    this.safeUpdate = safeUpdate;
  }

  @JsonInclude(Include.NON_DEFAULT)
  @JsonProperty("updated_stamp")
  private Date getUpdatedStamp() {
    return Boolean.TRUE.equals(safeUpdate) ? updatedAt : null;
  }

  @Override
  public String toString() {
    return "Ticket"
        + "{assigneeId="
        + assigneeId
        + ", id="
        + id
        + ", url='"
        + url
        + '\''
        + ", externalId='"
        + externalId
        + '\''
        + ", type='"
        + type
        + '\''
        + ", subject='"
        + subject
        + '\''
        + ", description='"
        + description
        + '\''
        + ", priority='"
        + priority
        + '\''
        + ", status='"
        + status
        + '\''
        + ", recipient='"
        + recipient
        + '\''
        + ", requesterId="
        + requesterId
        + ", submitterId="
        + submitterId
        + ", organizationId="
        + organizationId
        + ", groupId="
        + groupId
        + ", collaboratorIds="
        + collaboratorIds
        + ", forumTopicId="
        + forumTopicId
        + ", problemId="
        + problemId
        + ", hasIncidents="
        + hasIncidents
        + ", dueAt="
        + dueAt
        + ", tags="
        + tags
        + ", via="
        + via
        + ", customFields="
        + customFields
        + ", satisfactionRating="
        + satisfactionRating
        + ", sharingAgreementIds="
        + sharingAgreementIds
        + ", followupIds="
        + followupIds
        + ", ticketFormId="
        + ticketFormId
        + ", brandId="
        + brandId
        + ", isPublic="
        + isPublic
        + ", safeUpdate="
        + safeUpdate
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + '}';
  }

  public static class Requester {
    private Integer localeId;
    private String name;
    private String email;

    public Requester() {}

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
      return "Requester"
          + "{localeId="
          + localeId
          + ", name='"
          + name
          + '\''
          + ", email='"
          + email
          + '\''
          + '}';
    }
  }
}
