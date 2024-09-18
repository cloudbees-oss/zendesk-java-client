package org.zendesk.client.v2.model.events;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SurveyOfferedEvent extends Event {
    @JsonProperty("survey_type")
    private String surveyType;

    @JsonProperty("survey_id")
    private String surveyId;

    @JsonProperty("assigned_user_id")
    private Long assignedUserId;

    @JsonProperty("assigned_group_id")
    private Long assignedGroupId;

    public String getSurveyType() {
        return surveyType;
    }

    public void setSurveyType(String surveyType) {
        this.surveyType = surveyType;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public Long getAssignedGroupId() {
        return assignedGroupId;
    }

    public void setAssignedGroupId(Long assignedGroupId) {
        this.assignedGroupId = assignedGroupId;
    }
}
