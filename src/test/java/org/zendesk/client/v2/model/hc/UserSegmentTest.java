package org.zendesk.client.v2.model.hc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Testing UserType
 *
 * @author Maxim Savenko (maxim.savenko@gmail.com)
 */
public class UserSegmentTest {
    private UserSegment parseJson(byte[] json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, UserSegment.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    public void testParseUserSegment() throws ParseException {
        String json="{\n" +
                "        \"id\": 360001421932,\n" +
                "        \"user_type\": \"staff\",\n" +
                "        \"name\": \"Test User Segment\",\n" +
                "        \"group_ids\": [360004797451],\n" +
                "        \"organization_ids\": [],\n" +
                "        \"tags\": [\"vip\"],\n" +
                "        \"or_tags\": [],\n" +
                "        \"created_at\": \"2019-06-10T12:39:23Z\",\n" +
                "        \"updated_at\": \"2019-06-10T12:39:23Z\",\n" +
                "        \"built_in\": false\n" +
                "    }";
        UserSegment userSegment = parseJson(json.getBytes());

        assertNotNull(userSegment);
        assertEquals(UserSegment.class, userSegment.getClass());

        assertEquals((Long)360001421932L, userSegment.getId());
        assertEquals("Test User Segment", userSegment.getName());

        List<Long> ids = new ArrayList<>();
        ids.add(360004797451L);
        assertEquals(ids, userSegment.getGroupIds());
        assertEquals(new ArrayList<>(), userSegment.getOrganizationIds());

        List<String> tags = new ArrayList<>();
        tags.add("vip");
        assertEquals(tags, userSegment.getTags());
        assertEquals(new ArrayList<>(), userSegment.getOrTags());

        assertEquals(new Date(1560170363000L), userSegment.getCreatedAt());
        assertEquals(new Date(1560170363000L), userSegment.getUpdatedAt());
        assertEquals(false,userSegment.getBuiltIn());
    }
}