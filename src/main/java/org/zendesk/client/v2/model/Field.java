package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author stephenc
 * @since 05/04/2013 12:03
 */
public class Field implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String url;
    private String type;
    private String title;
    private String rawTitle;
    private String description;
    private String rawDescription;
    private Integer position;
    private Boolean active;
    private Boolean required;
    private Boolean collapsedForAgents;
    private String regexpForValidation;
    private String titleInPortal;
    private String rawTitleInPortal;
    private Boolean visibleInPortal;
    private Boolean editableInPortal;
    private Boolean requiredInPortal;
    private String tag;
    private Date createdAt;
    private Date updatedAt;
    private List<Option> systemFieldOptions;
    private List<Option> customFieldOptions;
    private Boolean removable;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @JsonProperty("collapsed_for_agents")
    public Boolean getCollapsedForAgents() {
        return collapsedForAgents;
    }

    public void setCollapsedForAgents(Boolean collapsedForAgents) {
        this.collapsedForAgents = collapsedForAgents;
    }

    @JsonProperty("created_at")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("custom_field_options")
    public List<Option> getCustomFieldOptions() {
        return customFieldOptions;
    }

    public void setCustomFieldOptions(List<Option> customFieldOptions) {
        this.customFieldOptions = customFieldOptions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("editable_in_portal")
    public Boolean getEditableInPortal() {
        return editableInPortal;
    }

    public void setEditableInPortal(Boolean editableInPortal) {
        this.editableInPortal = editableInPortal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @JsonProperty("raw_description")
    public String getRawDescription() {
        return rawDescription;
    }

    public void setRawDescription(String rawDescription) {
        this.rawDescription = rawDescription;
    }

    @JsonProperty("raw_title")
    public String getRawTitle() {
        return rawTitle;
    }

    public void setRawTitle(String rawTitle) {
        this.rawTitle = rawTitle;
    }

    @JsonProperty("raw_title_in_portal")
    public String getRawTitleInPortal() {
        return rawTitleInPortal;
    }

    public void setRawTitleInPortal(String rawTitleInPortal) {
        this.rawTitleInPortal = rawTitleInPortal;
    }

    @JsonProperty("regexp_for_validation")
    public String getRegexpForValidation() {
        return regexpForValidation;
    }

    public void setRegexpForValidation(String regexpForValidation) {
        this.regexpForValidation = regexpForValidation;
    }

    public Boolean getRemovable() {
        return removable;
    }

    public void setRemovable(Boolean removable) {
        this.removable = removable;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    @JsonProperty("required_in_portal")
    public Boolean getRequiredInPortal() {
        return requiredInPortal;
    }

    public void setRequiredInPortal(Boolean requiredInPortal) {
        this.requiredInPortal = requiredInPortal;
    }

    @JsonProperty("system_field_options")
    public List<Option> getSystemFieldOptions() {
        return systemFieldOptions;
    }

    public void setSystemFieldOptions(List<Option> systemFieldOptions) {
        this.systemFieldOptions = systemFieldOptions;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("title_in_portal")
    public String getTitleInPortal() {
        return titleInPortal;
    }

    public void setTitleInPortal(String titleInPortal) {
        this.titleInPortal = titleInPortal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("updated_at")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("visible_in_portal")
    public Boolean getVisibleInPortal() {
        return visibleInPortal;
    }

    public void setVisibleInPortal(Boolean visibleInPortal) {
        this.visibleInPortal = visibleInPortal;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Field");
        sb.append("{active=").append(active);
        sb.append(", id=").append(id);
        sb.append(", url='").append(url).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", position=").append(position);
        sb.append(", required=").append(required);
        sb.append(", collapsedForAgents=").append(collapsedForAgents);
        sb.append(", regexpForValidation='").append(regexpForValidation).append('\'');
        sb.append(", titleInPortal='").append(titleInPortal).append('\'');
        sb.append(", visibleInPortal=").append(visibleInPortal);
        sb.append(", editableInPortal=").append(editableInPortal);
        sb.append(", requiredInPortal=").append(requiredInPortal);
        sb.append(", tag='").append(tag).append('\'');
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", customFieldOptions=").append(customFieldOptions);
        sb.append('}');
        return sb.toString();
    }

    public static class Option implements Serializable {
        private static final long serialVersionUID = -8881532430230657120L;    
        private String name;
        private String value;

        private Option() {
        }

        public Option(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Option");
            sb.append("{name='").append(name).append('\'');
            sb.append(", value='").append(value).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
