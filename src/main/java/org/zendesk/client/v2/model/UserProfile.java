package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserProfile implements SearchResultEntity, Serializable {

    private Map<String, Object> attributes;
    private Date createdAt;
    private String id;
    private List<Identifier> identifiers;
    private String name;
    private String source;
    private String type;
    private Date updatedAt;
    private String userId;

    public UserProfile(
        Map<String, Object> attributes,
        Date createdAt,
        String id,
        List<Identifier> identifiers,
        String name,
        String source,
        String type,
        Date updatedAt,
        String userId) {

        this.attributes = attributes;
        this.createdAt = createdAt;
        this.id = id;
        this.identifiers = identifiers;
        this.name = name;
        this.source = source;
        this.type = type;
        this.updatedAt = updatedAt;
        this.userId = userId;
    }

    public UserProfile() {}

    public static class Identifier {

        private String type;
        private String value;

        public Identifier() {
        }

        public Identifier(String type, String value) {

            this.type = type;
            this.value = value;
        }

        public String getType() {

            return type;
        }

        public String getValue() {

            return value;
        }

        @Override
        public boolean equals(Object o) {

            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Identifier that = (Identifier) o;
            return Objects.equals(type, that.type) && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {

            return Objects.hash(type, value);
        }

        @Override
        public String toString() {

            return "Identifier{" +
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
        }
    }

    public Map<String, Object> getAttributes() {

        return attributes;
    }


    @JsonProperty("created_at")
    public Date getCreatedAt() {

        return createdAt;
    }

    public String getId() {

        return id;
    }

    public List<Identifier> getIdentifiers() {

        return identifiers;
    }

    public String getName() {

        return name;
    }

    public String getSource() {

        return source;
    }

    public String getType() {

        return type;
    }

    @JsonProperty("updated_at")
    public Date getUpdatedAt() {

        return updatedAt;
    }

    @JsonProperty("user_id")
    public String getUserId() {

        return userId;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserProfile that = (UserProfile) o;
        return Objects.equals(attributes, that.attributes) && Objects.equals(createdAt, that.createdAt) && Objects.equals(id, that.id) && Objects.equals(identifiers, that.identifiers) && Objects.equals(name, that.name) && Objects.equals(source, that.source) && Objects.equals(type, that.type) && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(attributes, createdAt, id, identifiers, name, source, type, updatedAt, userId);
    }

    @Override
    public String toString() {

        return "UserProfile{" +
            "attributes=" + attributes +
            ", created_at=" + createdAt +
            ", id='" + id + '\'' +
            ", identifiers=" + identifiers +
            ", name='" + name + '\'' +
            ", source='" + source + '\'' +
            ", type='" + type + '\'' +
            ", updated_at=" + updatedAt +
            ", user_id='" + userId + '\'' +
            '}';
    }
}
