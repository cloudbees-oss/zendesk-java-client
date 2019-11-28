package org.zendesk.client.v2.model.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * @author stephenc
 * @since 05/04/2013 11:55
 */
public class VoiceCommentEvent extends PublicPrivateEvent {

    private static final long serialVersionUID = 1L;

    private Map<String, Object> data;
    private String formattedFrom;
    private String formattedTo;
    private String body;
    private String htmlBody;
    private Boolean trusted;
    private Long authorId;
    private List<String> attachments;

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
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

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @JsonProperty("formatted_from")
    public String getFormattedFrom() {
        return formattedFrom;
    }

    public void setFormattedFrom(String formattedFrom) {
        this.formattedFrom = formattedFrom;
    }

    @JsonProperty("formatted_to")
    public String getFormattedTo() {
        return formattedTo;
    }

    public void setFormattedTo(String formattedTo) {
        this.formattedTo = formattedTo;
    }

    @Override
    public String toString() {
        return "VoiceCommentEvent" +
                "{attachments=" + attachments +
                ", data=" + data +
                ", formattedFrom='" + formattedFrom + '\'' +
                ", formattedTo='" + formattedTo + '\'' +
                ", body='" + body + '\'' +
                ", htmlBody='" + htmlBody + '\'' +
                ", trusted=" + trusted +
                ", authorId=" + authorId +
                '}';
    }
}
