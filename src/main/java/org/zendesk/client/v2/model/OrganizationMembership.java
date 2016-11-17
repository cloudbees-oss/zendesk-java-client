package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by schristou88 on 5/12/16.
 */
public class OrganizationMembership implements SearchResultEntity, Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long organizationId;
    private Boolean _default;
    private Date createdAt;
    private Date updatedAt;

    public OrganizationMembership(){
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @JsonProperty("organization_id")
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(final Long organizationId) {
        this.organizationId = organizationId;
    }

    @JsonProperty("default")
    public Boolean isDefault() {
        return _default;
    }

    public void setDefault(Boolean _default) {
        this._default = _default;
    }

    @JsonProperty("created_at")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("updated_at")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "OrganizationMembership{" +
                "id=" + id +
                ", userId=" + userId +
                ", organizationId=" + organizationId +
                ", default=" + _default +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
