package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Topic implements SearchResultEntity, Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String url;
    private String title;
    private String body;
    private TopicType topicType;
    private Long submitterId;
    private Long updaterId;
    private Long forumId;
    private Boolean locked;
    private Boolean pinned;
    private Boolean highlighted;
    private Boolean answered;
    private Long commentCount;
    private List<String> searchPhrases;
    private Long position;
    private List<String> tags;
    private Date createdAt;
    private Date updatedAt;
    private List<Attachment> attachments;
    private List<Attachment.Upload> uploads;

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    @JsonProperty("topic_type")
    public TopicType getTopicType() {
        return topicType;
    }

    public void setTopicType(final TopicType topicType) {
        this.topicType = topicType;
    }

    @JsonProperty("submitter_id")
    public Long getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(final Long submitterId) {
        this.submitterId = submitterId;
    }

    @JsonProperty("updater_id")
    public Long getUpdaterId() {
        return updaterId;
    }

    public void setUpdaterId(final Long updaterId) {
        this.updaterId = updaterId;
    }

    @JsonProperty("forum_id")
    public Long getForumId() {
        return forumId;
    }

    public void setForumId(final Long forumId) {
        this.forumId = forumId;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(final Boolean locked) {
        this.locked = locked;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public void setPinned(final Boolean pinned) {
        this.pinned = pinned;
    }

    public Boolean getHighlighted() {
        return highlighted;
    }

    public void setHighlighted(final Boolean highlighted) {
        this.highlighted = highlighted;
    }

    public Boolean getAnswered() {
        return answered;
    }

    public void setAnswered(final Boolean answered) {
        this.answered = answered;
    }

    @JsonProperty("comment_count")
    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(final Long commentCount) {
        this.commentCount = commentCount;
    }

    @JsonProperty("search_phrases")
    public List<String> getSearchPhrases() {
        return searchPhrases;
    }

    public void setSearchPhrases(final List<String> searchPhrases) {
        this.searchPhrases = searchPhrases;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(final Long position) {
        this.position = position;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(final List<String> tags) {
        this.tags = tags;
    }

    @JsonProperty("created_at")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("updated_at")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(final List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<Attachment.Upload> getUploads() {
        return uploads;
    }

    public void setUploads(final List<Attachment.Upload> uploads) {
        this.uploads = uploads;
    }

}
