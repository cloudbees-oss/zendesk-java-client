package org.zendesk.client.v2.model;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ajarnold87
 * @since 10/20/2020
 */
public enum Role {
    END_USER("end-user"),
    AGENT("agent"),
    ADMIN("admin");

    private final String equivalent;

    Role(String equivalent) {
        this.equivalent = equivalent;
    }

    private static final Map<String, Role> stringRoleMap = Arrays.stream(values())
        .collect(Collectors.toMap(Enum::name, Function.identity()));

    public static Role forEquivalent( final String role) {
        return stringRoleMap.get(role);
    }

    @Override
    public String toString() {
        return this.name();
    }
}

