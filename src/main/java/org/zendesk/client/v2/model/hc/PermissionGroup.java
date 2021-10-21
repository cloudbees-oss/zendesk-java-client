package org.zendesk.client.v2.model.hc;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.zendesk.client.v2.model.SearchResultEntity;

import java.util.Date;
import java.util.List;

/**
 * Management Permission Group
 * A management permission group defines which agents can create, update, archive, and publish articles.
 * https://developer.zendesk.com/rest_api/docs/help_center/permission_groups
 * @author Maxim Savenko (maxim.savenko@gmail.com)
 */
public class PermissionGroup implements SearchResultEntity {
    /** Automatically assigned when the permission group is created */
    private Long id;
    /** Permission group name */
    private String name;
    /** The ids of user segments that have edit privileges */
    private List<Long> edit;
    /** The ids of user segments that have publish privileges */
    private List<Long> publish;
    /** When the permission group was created */
    @JsonProperty("created_at")
    private Date createdAt;
    /** When the permission group was last updated */
    @JsonProperty("updated_at")
    private Date updatedAt;
    /** Whether the permission group is built-in. Built-in permission groups cannot be modified */
    @JsonProperty("built_in")
    private Boolean builtIn;

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

    public List<Long> getEdit() {
        return edit;
    }

    public void setEdit(List<Long> edit) {
        this.edit = edit;
    }

    public List<Long> getPublish() {
        return publish;
    }

    public void setPublish(List<Long> publish) {
        this.publish = publish;
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

    public Boolean getBuiltIn() {
        return builtIn;
    }

    public void setBuiltIn(Boolean builtIn) {
        this.builtIn = builtIn;
    }

    @Override
    public String toString() {
        return "PermissionGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", edit=" + edit +
                ", publish=" + publish +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", builtIn=" + builtIn +
                '}';
    }
}
