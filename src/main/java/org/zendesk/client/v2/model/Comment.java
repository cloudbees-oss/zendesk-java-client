package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
* @author stephenc
* @since 09/04/2013 15:09
*/
public class Comment {
    private Integer id;
    private String body;
    private Integer authorId;
    private List<String> uploads;
    private List<Attachment> attachments;
    private Date createdAt;

    public Comment() {
    }

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
    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    @JsonProperty("created_at")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Comment{");
        sb.append("id=").append(id);
        sb.append(", body='").append(body).append('\'');
        sb.append(", authorId=").append(authorId);
        sb.append(", attachments=").append(attachments);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", uploads=").append(uploads);
        sb.append('}');
        return sb.toString();
    }
}
