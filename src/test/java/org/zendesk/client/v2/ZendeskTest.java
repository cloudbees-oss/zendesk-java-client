package org.zendesk.client.v2;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.junit.Test;

public class ZendeskTest {

  @Test
  public void customizeMapper() {
    var builder = new Zendesk.Builder("dummy");
    builder.customizeObjectMapper(
        om -> om.setSerializationInclusion(JsonInclude.Include.NON_EMPTY));

    try (var zd = builder.build()) {
      var mapper = zd.getMapper();
      assertThat(
          mapper.getSerializationConfig().getDefaultPropertyInclusion(),
          is(
              JsonInclude.Value.construct(
                  JsonInclude.Include.NON_EMPTY, JsonInclude.Include.NON_EMPTY)));
    }
  }
}
