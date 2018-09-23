package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @author caionovaes
 * @since 08/24/2018
 */
public class UserRelatedInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty(value = "assigned_tickets")
    private Integer assignedTickets;
    @JsonProperty(value = "requested_tickets")
    private Integer requestedTickets;
    private Integer topics;
    @JsonProperty(value = "topic_comments")
    private Integer topicComments;
    private Integer votes;
    private Integer subscriptions;
    @JsonProperty(value = "entry_subscriptions")
    private Integer entrySubscriptions;
    @JsonProperty(value = "forum_subscriptions")
    private Integer forumSubscriptions;
    @JsonProperty(value = "organization_subscriptions")
    private Integer organizationSubscriptions;
    @JsonProperty(value = "ccd_tickets")
    private Integer ccdTickets;

    public Integer getAssignedTickets() {
        return assignedTickets;
    }

    public void setAssignedTickets(Integer assignedTickets) {
        this.assignedTickets = assignedTickets;
    }

    public Integer getRequestedTickets() {
        return requestedTickets;
    }

    public void setRequestedTickets(Integer requestedTickets) {
        this.requestedTickets = requestedTickets;
    }

    public Integer getTopics() {
        return topics;
    }

    public void setTopics(Integer topics) {
        this.topics = topics;
    }

    public Integer getTopicComments() {
        return topicComments;
    }

    public void setTopicComments(Integer topicComments) {
        this.topicComments = topicComments;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public Integer getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Integer subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Integer getEntrySubscriptions() {
        return entrySubscriptions;
    }

    public void setEntrySubscriptions(Integer entrySubscriptions) {
        this.entrySubscriptions = entrySubscriptions;
    }

    public Integer getForumSubscriptions() {
        return forumSubscriptions;
    }

    public void setForumSubscriptions(Integer forumSubscriptions) {
        this.forumSubscriptions = forumSubscriptions;
    }

    public Integer getOrganizationSubscriptions() {
        return organizationSubscriptions;
    }

    public void setOrganizationSubscriptions(Integer organizationSubscriptions) {
        this.organizationSubscriptions = organizationSubscriptions;
    }

    public Integer getCcdTickets() {
        return ccdTickets;
    }

    public void setCcdTickets(Integer ccdTickets) {
        this.ccdTickets = ccdTickets;
    }

    @Override
    public String toString() {
        return "UserRelatedInfo{" +
                "assignedTickets=" + assignedTickets +
                ", requestedTickets=" + requestedTickets +
                ", topics=" + topics +
                ", topicComments=" + topicComments +
                ", votes=" + votes +
                ", subscriptions=" + subscriptions +
                ", entrySubscriptions=" + entrySubscriptions +
                ", forumSubscriptions=" + forumSubscriptions +
                ", organizationSubscriptions=" + organizationSubscriptions +
                ", ccdTickets=" + ccdTickets +
                '}';
    }
}
