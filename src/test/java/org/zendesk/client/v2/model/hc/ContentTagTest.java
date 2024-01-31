package org.zendesk.client.v2.model.hc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;

public class ContentTagTest {

  private ContentTag parseJson(byte[] json) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(json, ContentTag.class);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Test
  public void testParsePermissionGroup() throws ParseException {
    String json =
        "{\n"
            + "  \"created_at\": \"2022-10-13T12:34:56.000Z\",\n"
            + "  \"id\": \"01GFXGBX7YZ9ASWTCVMASTK8ZS\",\n"
            + "  \"name\": \"feature request\",\n"
            + "  \"updated_at\": \"2022-10-13T13:44:55.000Z\"\n"
            + "}";
    ContentTag ct = parseJson(json.getBytes());

    assertNotNull(ct);
    assertEquals(ContentTag.class, ct.getClass());
    assertEquals("01GFXGBX7YZ9ASWTCVMASTK8ZS", ct.getId());
    assertEquals("feature request", ct.getName());

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    Date created = simpleDateFormat.parse("2022-10-13 12:34:56 +0000");
    assertEquals(created, ct.getCreatedAt());
    Date updated = simpleDateFormat.parse("2022-10-13 13:44:55 +0000");
    assertEquals(updated, ct.getUpdatedAt());
  }
}
