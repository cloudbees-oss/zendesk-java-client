package org.zendesk.client.v2.model.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A word or string was redacted from a ticket comment
 * 
 * @author matthewtckr
 * @see <a href="https://developer.zendesk.com/rest_api/docs/core/ticket_audits#comment-redaction-event">Zendesk API Documentation</a>
 *
 */
public class CommentRedactionEvent extends Event {

  private static final long serialVersionUID = 1L;

  private Long commentId;

  @JsonProperty("comment_id")
  public Long getCommentId() {
    return commentId;
  }

  public void setCommentId( Long commentId ) {
    this.commentId = commentId;
  }

  @Override
  public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("CommentRedactionEvent");
      sb.append("{commentId=").append(commentId);
      sb.append('}');
      return sb.toString();
  }
}
