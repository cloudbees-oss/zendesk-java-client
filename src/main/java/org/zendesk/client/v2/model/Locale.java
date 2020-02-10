package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Locale 
 * https://developer.zendesk.com/rest_api/docs/support/locales
 */
public class Locale implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String url;
    private String locale;
    private String name;
    private String nativeName;
    private String presentationName;
    private Boolean rtl;
    private Date createdAt;
    private Date updatedAt;
    private Boolean defaultLocale;

    public Locale() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("locale")
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("native_name")
    public String getNativeName() {
        return nativeName;
    }

    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    @JsonProperty("presentation_name")
    public String getPresentationName() {
        return presentationName;
    }

    public void setPresentationName(String presentationName) {
        this.presentationName = presentationName;
    }

    @JsonProperty("rtl")
    public Boolean isRtl() {
        return rtl;
    }

    public void setRtl(Boolean isRtl) {
        this.rtl = rtl;
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

    @JsonProperty("default")
    public Boolean isDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Boolean isDefault) {
        this.defaultLocale = isDefault;
    }

    @Override
    public String toString() {
        return "Locale{" + "id=" + id +
                ", url='" + url + '\'' +
                ", locale=" + locale +
                ", name=" + name +
                ", nativeName=" + nativeName +
                ", presentationName=" + presentationName +
                ", rtl=" + rtl +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", default=" + defaultLocale +
                '}';
    }
}
