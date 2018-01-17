package org.zendesk.client.v2.model.hc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class ArticleAttachments {

    /**
     *  Automatically assigned when the article attachment is created
     */
    private Long id;

    /**
     *  The API url of this article attachment
     */
    private String url;

    /**
     *  Id of the associated article, if present
     */
    private Long articleId;

    /**
     *  The name of the file
     */
    private String fileName;

    /**
     *  A full URL where the attachment file can be downloaded
     */
    private String contentUrl;

    /**
     *  The content type of the file. Example: image/png
     */
    private String contentType;

    /**
     *  The size of the attachment file in bytes
     */
    private int size;

    /**
     *  If true, the attached file is shown in the dedicated admin UI for inline attachments and
     *  its url can be referenced in the HTML body of the article.
     *  If false, the attachment is listed in the list of attachments. Default is false
     */
    private boolean inline;

    /**
     *  The time at which the article attachment was created
     */
    private Date createdAt;

    /**
     *  The time at which the article attachment was last updated
     */
    private Date updatedAt;

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

    @JsonProperty("article_id")
    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    @JsonProperty("file_name")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @JsonProperty("content_url")
    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    @JsonProperty("content_type")
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @JsonProperty("inline")
    public boolean isInline() {
        return inline;
    }

    public void setInline(boolean inline) {
        this.inline = inline;
    }

    @JsonProperty("created_at")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("updated_at")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "ArticleAttachments{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", articleId=" + articleId +
                ", fileName='" + fileName + '\'' +
                ", contentUrl='" + contentUrl + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + size +
                ", inline=" + inline +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
