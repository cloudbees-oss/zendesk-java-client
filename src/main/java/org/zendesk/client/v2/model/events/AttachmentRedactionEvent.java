package org.zendesk.client.v2.model.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An attachment was redacted, or permanently deleted, from a ticket comment
 * 
 * @author matthewtckr
 * @see <a href="https://developer.zendesk.com/rest_api/docs/core/ticket_audits#attachment-redaction-event">Zendesk API Documentation</a>
 *
 */
public class AttachmentRedactionEvent extends Event {

  private static final long serialVersionUID = 1L;

  private Long attachmentId;
  private Long commentId;

  @JsonProperty("attachment_id")
  public Long getAttachmentId() {
    return attachmentId;
  }

  public void setAttachmentId( Long attachmentId ) {
    this.attachmentId = attachmentId;
  }

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
      sb.append("AttachmentRedactionEvent");
      sb.append("{attachmentId=").append(attachmentId);
      sb.append(", commentId=").append(commentId);
      sb.append('}');
      return sb.toString();
  }
}
