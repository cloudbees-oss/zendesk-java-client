package org.zendesk.client.v2.model.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.zendesk.client.v2.model.Attachment;

import java.util.List;

/**
 * @author stephenc
 * @since 05/04/2013 11:54
 */
public class CommentEvent extends PublicPrivateEvent {

    private static final long serialVersionUID = 1L;

    private String body;
    private String htmlBody;
    private Boolean trusted;
    private Long authorId;
    private List<Attachment> attachments;

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

    public boolean getTrusted() {
        return trusted;
    }

    public void setTrusted(Boolean trusted) {
        this.trusted = trusted;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CommentEvent");
        sb.append("{attachments=").append(attachments);
        sb.append(", body='").append(body).append('\'');
        sb.append(", htmlBody='").append(htmlBody).append('\'');
        sb.append(", trusted=").append(trusted);
        sb.append(", authorId=").append(authorId);
        sb.append('}');
        return sb.toString();
    }
}
