package org.zendesk.client.v2.model.hc;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Subscription {

    /** Automatically assigned when the subscription is created */
    private Long id;

    /** The API URL of the subscription */
    private String url;

    /** The id of the user who has this subscription */
    @JsonProperty("user_id")
    private Long userId;

    /** The id of the subscribed item */
    @JsonProperty("content_id")
    private Long contentId;

    /** The type of the subscribed item */
    @JsonProperty("content_type")
    private String contentType;

    /** The locale of the subscribed item */
    private String locale;

    /** Subscribe also to article comments. Only for section subscriptions */
    @JsonProperty("include_comments")
    private boolean includeComments;

    /** The time at which the subscription was created */
    @JsonProperty("created_at")
    private Date createdAt;

    /** The time at which the subscription was last updated */
    @JsonProperty("updated_at")
    private Date updatedAt;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl( String url ) {
        this.url = url;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId( Long userId ) {
        this.userId = userId;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId( Long contentId ) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType( String contentType ) {
        this.contentType = contentType;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale( String locale ) {
        this.locale = locale;
    }

    public boolean isIncludeComments() {
        return includeComments;
    }

    public void setIncludeComments( boolean includeComments ) {
        this.includeComments = includeComments;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt( Date createdAt ) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt( Date updatedAt ) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", userId='" + userId + '\'' +
                ", contentId='" + contentId + '\'' +
                ", contentType='" + contentType + '\'' +
                ", locale='" + locale + '\'' +
                ", includeComments='" + includeComments + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
