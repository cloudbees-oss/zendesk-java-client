package org.zendesk.client.v2.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AgentRole {

    private Long id;
    private String name;
    private String description;
    private Date createdAt;
    private Date updatedAt;

    /**
     * A series of permissions granted to agents in this role
     */
    private Map<String, Object> configuration;

    public Long getId() {
      return id;
    }

    public void setId( Long id ) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName( String name ) {
      this.name = name;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription( String description ) {
      this.description = description;
    }

    @JsonProperty("created_at")
    public Date getCreatedAt() {
      return createdAt;
    }

    public void setCreatedAt( Date createdAt ) {
      this.createdAt = createdAt;
    }

    @JsonProperty("updated_at")
    public Date getUpdatedAt() {
      return updatedAt;
    }

    public void setUpdatedAt( Date updatedAt ) {
      this.updatedAt = updatedAt;
    }

    public Map<String, Object> getConfiguration() {
      return configuration;
    }

    public void setConfiguration( Map<String, Object> configuration ) {
      this.configuration = configuration;
    }
}
