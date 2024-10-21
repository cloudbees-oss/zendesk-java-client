package org.zendesk.client.v2.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.zendesk.client.v2.Zendesk;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UserProfileTest {

    public static final Long USER_ID = 123L;
    public static final String PROFILE_NAME = "name";

    @Test
    public void serializeDeserialize() throws JsonProcessingException {
        ObjectMapper mapper = Zendesk.createMapper();
        String email = "user123@example.com";
        UserProfile profile = createSampleUserProfile(email);
        String serialized = mapper.writeValueAsString(profile);

        UserProfile deserialized = mapper.readValue(serialized, UserProfile.class);
        assertThat(deserialized.getName()).isEqualTo(PROFILE_NAME);
        assertThat(deserialized.getUserId()).isEqualTo("123");
        assertThat(deserialized.getIdentifiers()).contains(new UserProfile.Identifier("email", email));
    }

    private UserProfile createSampleUserProfile(String email) {
        var identifiers = List.of(new UserProfile.Identifier("email", email));

        return new UserProfile(
            Map.of("customer_ref", "ref123"),
            Date.from(Instant.now()),
            "12345",
            identifiers,
            PROFILE_NAME,
            "source",
            "type",
            Date.from(Instant.now()),
            USER_ID.toString()
        );
    }
}
