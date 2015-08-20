package org.zendesk.client.v2.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SuspendedTicket implements Serializable {

    private static final long serialVersionUID = 1L;

    /* Automatically assigned */
    private Long id;

    /* The API url of this ticket */
    private String url;

    /* The author id (if available), name and email */
    private Object author;

    /* The value of the subject field for this ticket */
    private String subject;

    /* The content that was flagged */
    private String content;

    /* Why the ticket was suspended */
    private String cause;

    /* The ID of the email, if available */
    @JsonProperty("message_id")
    private String messageId;

    /* The ticket ID this suspended email is associated with, if available */
    @JsonProperty("ticket_id")
    private Long ticketId;

    /* The original recipient e-mail address of the ticket */
    private String recipient;

    /* When this record was created */
    @JsonProperty("created_at")
    private Date createdAt;

    /* When this record last got updated */
    @JsonProperty("updated_at")
    private Date updatedAt;

    /* This object explains how the ticket was created */
    private Via via;

    /* The id of the brand this ticket is associated with - only applicable for enterprise accounts */
    @JsonProperty("brand_id")
    private Long brandId;

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

    public Object getAuthor() {
        return author;
    }

    public void setAuthor(Object author) {
        this.author = author;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
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

    public Via getVia() {
        return via;
    }

    public void setVia(Via via) {
        this.via = via;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }
}
