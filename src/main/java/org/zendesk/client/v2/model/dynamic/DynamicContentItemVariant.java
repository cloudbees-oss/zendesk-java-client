package org.zendesk.client.v2.model.dynamic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class DynamicContentItemVariant implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Automatically assigned when creating items */
    private Long id;

    /** The API url of the variant */
    private String url;

    /** The content of the variant */
    private String content;

    /** An active locale */
    @JsonProperty("locale_id")
    private Long localeId;

    /** If the variant is outdated */
    private Boolean outdated;

    /** If the variant is active and useable */
    private Boolean active;

    /** If the variant is the default for the item it belongs to */
    @JsonProperty("default")
    private Boolean isDefault;

    /** When the variant was created */
    @JsonProperty("created_at")
    private Date createdAt;

    /** When the variant was last updated */
    @JsonProperty("updated_at")
    private Date updatedAt;

    public DynamicContentItemVariant() {
    }

    public DynamicContentItemVariant(Long id, String url, String content, Long localeId, Boolean outdated, Boolean active, Boolean isDefault, Date createdAt, Date updatedAt) {
        this.id = id;
        this.url = url;
        this.content = content;
        this.localeId = localeId;
        this.outdated = outdated;
        this.active = active;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getLocaleId() {
        return this.localeId;
    }

    public void setLocaleId(Long localeId) {
        this.localeId = localeId;
    }

    public Boolean isOutdated() {
        return this.outdated;
    }

    public Boolean getOutdated() {
        return this.outdated;
    }

    public void setOutdated(Boolean outdated) {
        this.outdated = outdated;
    }

    public Boolean isActive() {
        return this.active;
    }

    public Boolean getActive() {
        return this.active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean isIsDefault() {
        return this.isDefault;
    }

    public Boolean getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public DynamicContentItemVariant id(Long id) {
        this.id = id;
        return this;
    }

    public DynamicContentItemVariant url(String url) {
        this.url = url;
        return this;
    }

    public DynamicContentItemVariant content(String content) {
        this.content = content;
        return this;
    }

    public DynamicContentItemVariant localeId(Long localeId) {
        this.localeId = localeId;
        return this;
    }

    public DynamicContentItemVariant outdated(Boolean outdated) {
        this.outdated = outdated;
        return this;
    }

    public DynamicContentItemVariant active(Boolean active) {
        this.active = active;
        return this;
    }

    public DynamicContentItemVariant isDefault(Boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

    public DynamicContentItemVariant createdAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public DynamicContentItemVariant updatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DynamicContentItemVariant)) {
            return false;
        }
        DynamicContentItemVariant variant = (DynamicContentItemVariant) o;
        return Objects.equals(id, variant.id) && Objects.equals(url, variant.url) && Objects.equals(content, variant.content) && Objects.equals(localeId, variant.localeId) && Objects.equals(outdated, variant.outdated) && Objects.equals(active, variant.active) && Objects.equals(isDefault, variant.isDefault) && Objects.equals(createdAt, variant.createdAt) && Objects.equals(updatedAt, variant.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, content, localeId, outdated, active, isDefault, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "DynamicContentItemVariant{" +
            " id='" + getId() + "'" +
            ", url='" + getUrl() + "'" +
            ", content='" + getContent() + "'" +
            ", localeId='" + getLocaleId() + "'" +
            ", outdated='" + isOutdated() + "'" +
            ", active='" + isActive() + "'" +
            ", isDefault='" + isIsDefault() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }


}
