package org.zendesk.client.v2.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.zendesk.client.v2.Zendesk;

public class UserProfileTest {

  public static final String USER_ID = "123";
  public static final String PROFILE_NAME = "name";
  public static final String EMAIL = "user123@example.com";

  @Test
  public void serializeDeserialize() throws JsonProcessingException {
    ObjectMapper mapper = Zendesk.createMapper();
    UserProfile profile = createSampleUserProfile();
    String serialized = mapper.writeValueAsString(profile);

    UserProfile deserialized = mapper.readValue(serialized, UserProfile.class);
    assertThat(deserialized.getName()).isEqualTo(PROFILE_NAME);
    assertThat(deserialized.getUserId()).isEqualTo(USER_ID);
    assertThat(deserialized.getIdentifiers())
        .first()
        .extracting(UserProfile.Identifier::getType, UserProfile.Identifier::getValue)
        .contains("email", EMAIL);
  }

  private UserProfile createSampleUserProfile() {
    var identifiers = List.of(new UserProfile.Identifier("email", EMAIL));

    return new UserProfile(
        Map.of("customer_ref", "ref123"),
        Date.from(Instant.now()),
        "12345",
        identifiers,
        PROFILE_NAME,
        "source",
        "type",
        Date.from(Instant.now()),
        USER_ID);
  }
}
