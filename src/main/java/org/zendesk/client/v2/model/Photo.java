package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author stephenc
 * @since 05/04/2013 15:36
 */
public class Photo {
    private Long id;
    private String fileName;
    private String contentUrl;
    private String contentType;
    private Long size;

    @JsonProperty("content_type")
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @JsonProperty("content_url")
    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    @JsonProperty("file_name")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Photo");
        sb.append("{contentType='").append(contentType).append('\'');
        sb.append(", id=").append(id);
        sb.append(", fileName='").append(fileName).append('\'');
        sb.append(", contentUrl='").append(contentUrl).append('\'');
        sb.append(", size=").append(size);
        sb.append('}');
        return sb.toString();
    }
}
