package org.zendesk.client.v2.model.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author stephenc
 * @since 05/04/2013 11:57
 */
public class SatisfactionRatingEvent extends Event {

    private static final long serialVersionUID = 1L;

    private String score;
    private Long assigneeId;
    private String body;

    @JsonProperty("assignee_id")
    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "SatisfactionRatingEvent" +
                "{assigneeId=" + assigneeId +
                ", score='" + score + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
