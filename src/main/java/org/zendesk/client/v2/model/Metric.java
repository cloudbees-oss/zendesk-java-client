package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jyrij
 */
public class Metric implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    protected Long id;
    @JsonProperty("ticket_id")
    protected Long ticketId;
    @JsonProperty("group_stations")
    protected Long groupStations;
    @JsonProperty("assignee_stations")
    protected Long assigneeStations;
    @JsonProperty("reopens")
    protected Long reopens;
    @JsonProperty("replies")
    protected Long replies;
    @JsonProperty("assignee_updated_at")
    protected Date assigneeUpdatedAt;
    @JsonProperty("requester_updated_at")
    protected Date requesterUpdatedAt;
    @JsonProperty("status_updated_at")
    protected Date lastUpdatedAt;
    @JsonProperty("initially_assigned_at")
    protected Date initiallyUpdatedAt;
    @JsonProperty("assigned_at")
    protected Date assignedAt;
    @JsonProperty("solved_at")
    protected Date solvedAt;
    @JsonProperty("latest_comment_added_at")
    protected Date lastCommentAddedAt;
    @JsonProperty("reply_time_in_minutes")
    protected ZendeskComboMinutes replyTimeMinutes;
    @JsonProperty("first_resolution_time_in_minutes")
    protected ZendeskComboMinutes firstResolutionTimeMinutes;
    @JsonProperty("full_resolution_time_in_minutes")
    protected ZendeskComboMinutes fullResolutionTimeMinutes;
    @JsonProperty("agent_wait_time_in_minutes")
    protected ZendeskComboMinutes agentWaitTimeMinutes;
    @JsonProperty("requester_wait_time_in_minutes")
    protected ZendeskComboMinutes requesterWaitTimeMinutes;
    @JsonProperty("on_hold_time_in_minutes")
    protected ZendeskComboMinutes onHoldTimeMinutes;
    @JsonProperty("created_at")
    protected Date createdAt;
    @JsonProperty("updated_at")
    protected Date updatedAt;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getSolvedAt() {
        return solvedAt;
    }

    public void setSolvedAt(Date solvedAt) {
        this.solvedAt = solvedAt;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Long getGroupStations() {
        return groupStations;
    }

    public void setGroupStations(Long groupStations) {
        this.groupStations = groupStations;
    }

    public Long getAssigneeStations() {
        return assigneeStations;
    }

    public void setAssigneeStations(Long assigneeStations) {
        this.assigneeStations = assigneeStations;
    }

    public Long getReopens() {
        return reopens;
    }

    public void setReopens(Long reopens) {
        this.reopens = reopens;
    }

    public Long getReplies() {
        return replies;
    }

    public void setReplies(Long replies) {
        this.replies = replies;
    }

    public Date getAssigneeUpdatedAt() {
        return assigneeUpdatedAt;
    }

    public void setAssigneeUpdatedAt(Date assigneeUpdatedAt) {
        this.assigneeUpdatedAt = assigneeUpdatedAt;
    }

    public Date getRequesterUpdatedAt() {
        return requesterUpdatedAt;
    }

    public void setRequesterUpdatedAt(Date requesterUpdatedAt) {
        this.requesterUpdatedAt = requesterUpdatedAt;
    }

    public Date getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public Date getInitiallyUpdatedAt() {
        return initiallyUpdatedAt;
    }

    public void setInitiallyUpdatedAt(Date initiallyUpdatedAt) {
        this.initiallyUpdatedAt = initiallyUpdatedAt;
    }

    public Date getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Date assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Date getLastCommentAddedAt() {
        return lastCommentAddedAt;
    }

    public void setLastCommentAddedAt(Date lastCommentAddedAt) {
        this.lastCommentAddedAt = lastCommentAddedAt;
    }

    public ZendeskComboMinutes getReplyTimeMinutes() {
        return replyTimeMinutes;
    }
    
    public ZendeskComboMinutes getFirstResolutionTimeMinutes() {
        return firstResolutionTimeMinutes;
    }

    public void setFirstResolutionTimeMinutes(ZendeskComboMinutes firstResolutionTimeMinutes) {
        this.firstResolutionTimeMinutes = firstResolutionTimeMinutes;
    }
    
    public void setReplyTimeMinutes(ZendeskComboMinutes replyTimeMinutes) {
        this.replyTimeMinutes = replyTimeMinutes;
    }

    public ZendeskComboMinutes getFullResolutionTimeMinutes() {
        return fullResolutionTimeMinutes;
    }

    public void setFullResolutionTimeMinutes(ZendeskComboMinutes fullResolutionTimeMinutes) {
        this.fullResolutionTimeMinutes = fullResolutionTimeMinutes;
    }

    public ZendeskComboMinutes getAgentWaitTimeMinutes() {
        return agentWaitTimeMinutes;
    }

    public void setAgentWaitTimeMinutes(ZendeskComboMinutes agentWaitTimeMinutes) {
        this.agentWaitTimeMinutes = agentWaitTimeMinutes;
    }

    public ZendeskComboMinutes getRequesterWaitTimeMinutes() {
        return requesterWaitTimeMinutes;
    }

    public void setRequesterWaitTimeMinutes(ZendeskComboMinutes requesterWaitTimeMinutes) {
        this.requesterWaitTimeMinutes = requesterWaitTimeMinutes;
    }
    
    public ZendeskComboMinutes getOnHoldTimeMinutes() {
        return onHoldTimeMinutes;
    }

    public void setOnHoldTimeMinutes(ZendeskComboMinutes onHoldTimeMinutes) {
        this.onHoldTimeMinutes = onHoldTimeMinutes;
    }

    @Override
    public String toString() {
        return "Metric{" +
                "id=" + id +
                ", ticketId=" + ticketId +
                ", groupStations=" + groupStations +
                ", assigneeStations=" + assigneeStations +
                ", reopens=" + reopens +
                ", replies=" + replies +
                ", assigneeUpdatedAt=" + assigneeUpdatedAt +
                ", requesterUpdatedAt=" + requesterUpdatedAt +
                ", lastUpdatedAt=" + lastUpdatedAt +
                ", initiallyUpdatedAt=" + initiallyUpdatedAt +
                ", assignedAt=" + assignedAt +
                ", solvedAt=" + solvedAt +
                ", lastCommentAddedAt=" + lastCommentAddedAt +
                ", replyTimeMinutes=" + replyTimeMinutes +
                ", fullResolutionTimeMinutes=" + fullResolutionTimeMinutes +
                ", agentWaitTimeMinutes=" + agentWaitTimeMinutes +
                ", requesterWaitTimeMinutes=" + requesterWaitTimeMinutes +
                ", createdAt=" + createdAt +
                '}';
    }

    public class ZendeskComboMinutes {

        @JsonProperty("calendar")
        protected Long calendarMinutes;
        @JsonProperty("business")
        protected Long businessMinutes;

        public ZendeskComboMinutes() {};

        public Long getCalendarMinutes() {
            return calendarMinutes;
        }

        public void setCalendarMinutes(Long calendarMinutes) {
            this.calendarMinutes = calendarMinutes;
        }

        public Long getBusinessMinutes() {
            return businessMinutes;
        }

        public void setBusinessMinutes(Long businessMinutes) {
            this.businessMinutes = businessMinutes;
        }
    }

}
