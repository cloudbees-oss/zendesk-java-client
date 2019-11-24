package org.zendesk.client.v2.model.dynamic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DynamicContentItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Automatically assigned when creating items */
    private Long id;

    /** The API url of this item */
    private String url;

    /** The unique name of the item */
    private String name;

    /** Automatically generated placeholder for the item, derived from name */
    private String placeholder;

    /** The default locale for the item. Must be one of the locales the account has active. */
    @JsonProperty("default_locale_id")
    private Long defaultLocaleId;

    /** Indicates the item has outdated variants within it */
    private Boolean outdated;

    /** When this record was created */
    @JsonProperty("created_at")
    private Date createdAt;

    /** When this record last got updated */
    @JsonProperty("updated_at")
    private Date updatedAt;

    /** All variants within this item */
    private List<DynamicContentItemVariant> variants;

    public DynamicContentItem() {
    }

    public DynamicContentItem(Long id, String url, String name, String placeholder, Long defaultLocaleId, Boolean outdated,
            Date createdAt, Date updatedAt, List<DynamicContentItemVariant> variants) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.placeholder = placeholder;
        this.defaultLocaleId = defaultLocaleId;
        this.outdated = outdated;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.variants = variants;
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceholder() {
        return this.placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public Long getDefaultLocaleId() {
        return this.defaultLocaleId;
    }

    public void setDefaultLocaleId(Long defaultLocaleId) {
        this.defaultLocaleId = defaultLocaleId;
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

    public List<DynamicContentItemVariant> getVariants() {
        return this.variants;
    }

    public void setVariants(List<DynamicContentItemVariant> variants) {
        this.variants = variants;
    }

    public DynamicContentItem id(Long id) {
        this.id = id;
        return this;
    }

    public DynamicContentItem url(String url) {
        this.url = url;
        return this;
    }

    public DynamicContentItem name(String name) {
        this.name = name;
        return this;
    }

    public DynamicContentItem placeholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public DynamicContentItem defaultLocaleId(Long defaultLocaleId) {
        this.defaultLocaleId = defaultLocaleId;
        return this;
    }

    public DynamicContentItem outdated(Boolean outdated) {
        this.outdated = outdated;
        return this;
    }

    public DynamicContentItem createdAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public DynamicContentItem updatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public DynamicContentItem variants(List<DynamicContentItemVariant> variants) {
        this.variants = variants;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DynamicContentItem)) {
            return false;
        }
        DynamicContentItem item = (DynamicContentItem) o;
        return Objects.equals(id, item.id) && Objects.equals(url, item.url) && Objects.equals(name, item.name)
                && Objects.equals(placeholder, item.placeholder)
                && Objects.equals(defaultLocaleId, item.defaultLocaleId) && Objects.equals(outdated, item.outdated)
                && Objects.equals(createdAt, item.createdAt) && Objects.equals(updatedAt, item.updatedAt)
                && Objects.equals(variants, item.variants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, name, placeholder, defaultLocaleId, outdated, createdAt, updatedAt, variants);
    }

    @Override
    public String toString() {
        return "DynamicContentItem{" +
        " id='" + getId() + "'" +
        ", url='" + getUrl() + "'" +
        ", name='" + getName() + "'" +
        ", placeholder='" + getPlaceholder() + "'" +
        ", defaultLocaleId='" + getDefaultLocaleId() + "'" +
        ", outdated='" + isOutdated() + "'" +
        ", createdAt='" + getCreatedAt() + "'" +
        ", updatedAt='" + getUpdatedAt() + "'" +
        ", variants='" + getVariants() + "'" +
        "}";
    }


}
