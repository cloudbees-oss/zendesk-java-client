package org.zendesk.client.v2.model.hc;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.zendesk.client.v2.model.SearchResultEntity;

import java.util.Date;
import java.util.List;

public class Article implements SearchResultEntity {
    /** Automatically assigned when the article is created */
    private Long id;

    /** The API url of the article */
    private String url;

    /** The url of the article in Help Center */
    @JsonProperty("html_url")
    private String htmlUrl;

    /** The title of the article */
    private String title;

    /** The HTML body of the article */
    private String body;

    /** The locale that the article is being displayed in */
    private String locale;

    /** The source (default) locale of the article */
    @JsonProperty("source_locale")
    private String sourceLocale;

    /** The id of the user who wrote the article (set to the user who made the request on create by default) */
    @JsonProperty("author_id")
    private Long authorId;

    /** True if comments are disabled; false otherwise */
    @JsonProperty("comments_disabled")
    private Boolean commentsDisabled;

    /** Whether the source (default) translation of the article is out of date */
    private Boolean outdated;

    /** An array of label names associated with this article. By default no label names are used. Only available on certain plans */
    @JsonProperty("label_names")
    private List<String> labelNames;

    /** True if the translation for the current locale is a draft; false otherwise. false by default. */
    private Boolean draft;

    /** True if this article is promoted; false otherwise. false by default */
    private Boolean promoted;

    /** The position of this article in the article list. 0 by default */
    private Long position;

    /** The total sum of votes on this article */
    @JsonProperty("vote_sum")
    private Long voteSum;

    /** The number of votes cast on this article */
    @JsonProperty("vote_count")
    private Long voteCount;

    /** The id of the section to which this article belongs */
    @JsonProperty("section_id")
    private Long sectionId;

    /** The time the article was created */
    @JsonProperty("created_at")
    private Date createdAt;

    /** The time the article was last updated */
    @JsonProperty("updated_at")
    private Date updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Boolean getCommentsDisabled() {
        return commentsDisabled;
    }

    public void setCommentsDisabled(Boolean commentsDisabled) {
        this.commentsDisabled = commentsDisabled;
    }

    public Boolean getOutdated() {
        return outdated;
    }

    public void setOutdated(Boolean outdated) {
        this.outdated = outdated;
    }

    public List<String> getLabelNames() {
        return labelNames;
    }

    public void setLabelNames(List<String> labelNames) {
        this.labelNames = labelNames;
    }

    public Boolean getDraft() {
        return draft;
    }

    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    public Boolean getPromoted() {
        return promoted;
    }

    public void setPromoted(Boolean promoted) {
        this.promoted = promoted;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public Long getVoteSum() {
        return voteSum;
    }

    public void setVoteSum(Long voteSum) {
        this.voteSum = voteSum;
    }

    public Long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Long voteCount) {
        this.voteCount = voteCount;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
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
        return "Article{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", locale='" + locale + '\'' +
                ", sourceLocale='" + sourceLocale + '\'' +
                ", authorId=" + authorId +
                ", commentsDisabled=" + commentsDisabled +
                ", outdated=" + outdated +
                ", labelNames=" + labelNames +
                ", draft=" + draft +
                ", promoted=" + promoted +
                ", position=" + position +
                ", voteSum=" + voteSum +
                ", voteCount=" + voteCount +
                ", sectionId=" + sectionId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
