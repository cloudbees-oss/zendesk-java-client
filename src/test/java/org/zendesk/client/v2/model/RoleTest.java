package org.zendesk.client.v2.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.Test;
import org.zendesk.client.v2.Zendesk;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author ajarnold87
 * @since 10/20/2020
 */
public class RoleTest {
  @Test
  public void serializeAsLowerCase() throws Exception {
    ObjectMapper mapper = Zendesk.createMapper();
    assertThat(mapper.writeValueAsString(Role.END_USER), is("\"" + Role.END_USER.name() + "\""));
  }

  @Test
  public void deserializeFromLowerCase() throws Exception {
    ObjectMapper mapper = Zendesk.createMapper();
    ObjectReader reader = mapper.readerFor(Role.class);
    assertThat(reader.readValue("\"" + Role.END_USER.name() + "\""), is((Object)Role.END_USER));
  }
}
