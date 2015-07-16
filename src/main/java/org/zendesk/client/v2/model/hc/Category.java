package org.zendesk.client.v2.model.hc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class Category {
    /** Automatically assigned when creating categories */
    private Long id;

    /** The name of the category */
    private String name;

    /** The description of the category */
    private String description;

    /** The locale that the category is being displayed in */
    private String locale;

    /** The source (default) locale of the category */
    @JsonProperty("source_locale")
    private String sourceLocale;

    /** The API url of this category */
    private String url;

    /** The url of this category in Help Center */
    @JsonProperty("html_url")
    private String htmlUrl;

    /** Whether the category is out of date */
    private Boolean outdated;

    /** The position of this category relative to other categories */
    private Long position;

    /** The ids of all translations of this category */
    @JsonProperty("translation_ids")
    private List<String> translation_ids;

    /** The time the category was created */
    @JsonProperty("created_at")
    private Date createdAt;

    /** The time the category was last updated */
    @JsonProperty("updated_at")
    private Date updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getSourceLocale() {
        return sourceLocale;
    }

    public void setSourceLocale(String sourceLocale) {
        this.sourceLocale = sourceLocale;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public Boolean getOutdated() {
        return outdated;
    }

    public void setOutdated(Boolean outdated) {
        this.outdated = outdated;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public List<String> getTranslation_ids() {
        return translation_ids;
    }

    public void setTranslation_ids(List<String> translation_ids) {
        this.translation_ids = translation_ids;
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

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", locale='" + locale + '\'' +
                ", sourceLocale='" + sourceLocale + '\'' +
                ", url='" + url + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", outdated=" + outdated +
                ", position=" + position +
                ", translation_ids=" + translation_ids +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
