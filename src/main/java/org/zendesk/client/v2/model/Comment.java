package org.zendesk.client.v2.model;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXTERNAL_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.zendesk.client.v2.model.comments.VoiceComment;

/**
 * @author stephenc
 * @since 09/04/2013 15:09
 */
@JsonTypeInfo(use = NAME, include = EXTERNAL_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = Comment.class, name = "Comment"),
  @JsonSubTypes.Type(value = VoiceComment.class, name = "VoiceComment"),
  @JsonSubTypes.Type(value = VoiceComment.class, name = "TpeVoiceComment")
})
public class Comment implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private String body;
  private String htmlBody;
  private Long authorId;
  private List<String> uploads;
  private List<Attachment> attachments;
  private Date createdAt;
  private Boolean publicComment;
  private CommentType type;
  private Via via;

  public Comment() {}

  public Comment(String body) {
    this.body = body;
  }

  public Comment(String body, String... uploads) {
    this.body = body;
    this.uploads = uploads.length == 0 ? null : Arrays.asList(uploads);
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  @JsonProperty("html_body")
  public String getHtmlBody() {
    return htmlBody;
  }

  public void setHtmlBody(String htmlBody) {
    this.htmlBody = htmlBody;
  }

  public List<String> getUploads() {
    return uploads;
  }

  public void setUploads(List<String> uploads) {
    this.uploads = uploads;
  }

  public List<Attachment> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<Attachment> attachments) {
    this.attachments = attachments;
  }

  @JsonProperty("author_id")
  public Long getAuthorId() {
    return authorId;
  }

  public void setAuthorId(Long authorId) {
    this.authorId = authorId;
  }

  @JsonProperty("created_at")
  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @JsonProperty("public")
  public Boolean isPublic() {
    return publicComment;
  }

  public void setPublic(Boolean isPublic) {
    this.publicComment = isPublic;
  }

  @JsonProperty("type")
  public CommentType getType() {
    return type;
  }

  public void setType(CommentType type) {
    this.type = type;
  }

  @JsonProperty("via")
  public Via getVia() {
    return via;
  }

  public void setVia(Via via) {
    this.via = via;
  }

  @Override
  public String toString() {
    return "Comment{"
        + "id="
        + id
        + ", body='"
        + body
        + '\''
        + ", authorId="
        + authorId
        + ", attachments="
        + attachments
        + ", createdAt="
        + createdAt
        + ", uploads="
        + uploads
        + ", via="
        + via
        + '}';
  }
}
