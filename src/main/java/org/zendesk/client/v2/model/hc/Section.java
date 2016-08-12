package org.zendesk.client.v2.model.hc;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Section {
    /** Automatically assigned when creating subscriptions */
    private Long id;

    /** The name of the section */
    private String name;

    /** The locale in which the section is displayed */
    private String locale;

    /** The source (default) locale of the section */
    @JsonProperty("source_locale")
    private String sourceLocale;

    /** The API url of this section */
    private String url;

    /** The url of this section in Help Center */
    @JsonProperty("html_url")
    private String htmlUrl;

    /** The id of the category to which this section belongs */
    @JsonProperty("category_id")
    private Long categoryId;

    /** Whether the section is out of date */
    private Boolean outdated;

    /** The position of this section in the section list. By default the section is added to the end of the list */
    private Long position;

    /** The ids of all translations of this section */
    @JsonProperty("translation_ids")
    private List<String> translation_ids;

    /** The time the section was created */
    @JsonProperty("created_at")
    private Date createdAt;

    /** The time the section was last updated */
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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
        return "Section{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", locale='" + locale + '\'' +
                ", sourceLocale='" + sourceLocale + '\'' +
                ", url='" + url + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", outdated=" + outdated +
                ", position=" + position +
                ", translation_ids=" + translation_ids +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
