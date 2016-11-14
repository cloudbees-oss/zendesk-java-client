package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * @since 09/11/2016 13:08
 * @version FIXME
 */
public class Brand {

    private String url;
    private Long id;
    private String name;
    @JsonProperty("brand_url")
    private String brandUrl;
    @JsonProperty("has_help_center")
    private boolean hasHelpCenter;
    @JsonProperty("help_center_state")
    private String helpCenterState;
    @JsonProperty("active")
    private boolean isActive;
    @JsonProperty("default")
    private boolean isDefault;
    private Attachment logo;
    @JsonProperty("ticket_form_ids")
    private List<Long> ticketFormIds;
    @JsonProperty("created_at")
    private Date createdAt;
    @JsonProperty("updated_at")
    private Date updatedAt;
    private String subdomain;
    @JsonProperty("host_mapping")
    private String hostMapping;

    public Brand() {
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

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getHostMapping() {
        return hostMapping;
    }

    public void setHostMapping(String hostMapping) {
        this.hostMapping = hostMapping;
    }

    public String getSignatureTemplate() {
        return signatureTemplate;
    }

    public void setSignatureTemplate(String signatureTemplate) {
        this.signatureTemplate = signatureTemplate;
    }

    @JsonProperty("signature_template")
    private String signatureTemplate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrandUrl() {
        return brandUrl;
    }

    public void setBrandUrl(String brandUrl) {
        this.brandUrl = brandUrl;
    }

    public boolean isHasHelpCenter() {
        return hasHelpCenter;
    }

    public void setHasHelpCenter(boolean hasHelpCenter) {
        this.hasHelpCenter = hasHelpCenter;
    }

    public String getHelpCenterState() {
        return helpCenterState;
    }

    public void setHelpCenterState(String helpCenterState) {
        this.helpCenterState = helpCenterState;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public Attachment getLogo() {
        return logo;
    }

    public void setLogo(Attachment logo) {
        this.logo = logo;
    }

    public List<Long> getTicketFormIds() {
        return ticketFormIds;
    }

    public void setTicketFormIds(List<Long> ticketFormIds) {
        this.ticketFormIds = ticketFormIds;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
