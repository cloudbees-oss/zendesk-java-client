package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * https://developer.zendesk.com/rest_api/docs/core/users#show-compliance-deletion-statuses
 */
public class ComplianceDeletionStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private String action;
    private String application;
    @JsonProperty("account_subdomain")
    private String accountSubdomain;
    @JsonProperty("executer_id")
    private long executerId;
    @JsonProperty("user_id")
    private long userId;
    @JsonProperty("account_id")
    private long accountId;
    @JsonProperty("created_at")
    private Date createdAt;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getAccountSubdomain() {
        return accountSubdomain;
    }

    public void setAccountSubdomain(String accountSubdomain) {
        this.accountSubdomain = accountSubdomain;
    }

    public long getExecuterId() {
        return executerId;
    }

    public void setExecuterId(long executerId) {
        this.executerId = executerId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public Date getCreatedAt() {
        return new Date(createdAt.getTime());
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ComplianceDeletionStatus{" +
                "action='" + action + '\'' +
                ", application='" + application + '\'' +
                ", accountSubdomain='" + accountSubdomain + '\'' +
                ", executerId=" + executerId +
                ", userId=" + userId +
                ", accountId=" + accountId +
                ", createdAt=" + createdAt +
                '}';
    }
}