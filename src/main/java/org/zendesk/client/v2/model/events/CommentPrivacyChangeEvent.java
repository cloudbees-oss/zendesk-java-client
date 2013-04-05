package org.zendesk.client.v2.model.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author stephenc
 * @since 05/04/2013 11:55
 */
public class CommentPrivacyChangeEvent extends PublicPrivateEvent {
    private Integer commentId;

    @JsonProperty("comment_id")
    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CommentPrivacyChangeEvent");
        sb.append("{commentId=").append(commentId);
        sb.append('}');
        return sb.toString();
    }
}
