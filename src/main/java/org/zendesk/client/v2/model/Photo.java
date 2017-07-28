package org.zendesk.client.v2.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author stephenc
 * @since 05/04/2013 15:36
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String fileName;
    private String url;
    private String contentUrl;
    private String mappedContentUrl;
    private String contentType;
    private Long size;
    private Long width;
    private Long height;
    private Boolean inline;

    @JsonProperty("content_type")
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("content_url")
    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    @JsonProperty("mapped_content_url")
    public String getMappedContentUrl() {
        return mappedContentUrl;
    }

    public void setMappedContentUrl(String mappedContentUrl) {
        this.mappedContentUrl = mappedContentUrl;
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

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public Long getHeight() {
        return height;
    }

    public Boolean getInline() {
        return inline;
    }

    public void setInline(Boolean inline) {
        this.inline = inline;
    }

    public void setHeight(Long height) {
        this.height = height;
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
        sb.append(", width=").append(width);
        sb.append(", height=").append(height);
        sb.append(", inline=").append(inline);
        sb.append('}');
        return sb.toString();
    }
}
