package org.zendesk.client.v2.model.hc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Testing Permission Groups
 * @author Maxim Savenko (maxim.savenko@gmail.com)
 */
public class PermissionGroupTest {

    private PermissionGroup parseJson(byte[] json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, PermissionGroup.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    public void testParsePermissionGroup() throws ParseException {
        String json="{\n" +
                "    \"id\": 2939112,\n" +
                "    \"name\": \"ApiGroup\",\n" +
                "    \"built_in\": false,\n" +
                "    \"publish\": [360001413871],\n" +
                "    \"created_at\": \"2019-06-10T12:39:25Z\",\n" +
                "    \"updated_at\": \"2020-11-04T11:30:42Z\",\n" +
                "    \"edit\": [360001413871]\n" +
                "}";
        PermissionGroup pg = parseJson(json.getBytes());

        assertNotNull(pg);
        assertEquals(PermissionGroup.class, pg.getClass());
        assertEquals((Long)2939112L, pg.getId());
        assertEquals("ApiGroup", pg.getName());
        assertEquals(false, pg.getBuiltIn());
        List<Long> ids = new ArrayList<>();
        ids.add(360001413871L);
        assertEquals(ids, pg.getPublish());
        assertEquals(ids, pg.getEdit());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        Date created = simpleDateFormat.parse("2019-06-10 12:39:25 +0000");
        assertEquals(created, pg.getCreatedAt());
        Date updated = simpleDateFormat.parse("2020-11-04 11:30:42 +0000");
        assertEquals(updated, pg.getUpdatedAt());
    }

}
