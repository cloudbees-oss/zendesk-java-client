package org.zendesk.client.v2.model.hc;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User Type
 *
 * @author Maxim Savenko (maxim.savenko@gmail.com)
 */
public enum UserType {
    @JsonProperty("signed_in_users")
    SIGNED_IN_USERS("signed_in_users"),
    @JsonProperty("staff")
    STAFF("staff");

    private final String name;

    UserType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}