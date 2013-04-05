package org.zendesk.client.v2.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;
import org.zendesk.client.v2.ZenDesk;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author stephenc
 * @since 05/04/2013 09:08
 */
public class StatusTest {
    @Test
    public void serializeAsLowercase() throws Exception {
        ObjectMapper mapper = ZenDesk.createMapper();
        assertThat(mapper.writeValueAsString(Status.PENDING), is("\"" + Status.PENDING.name().toLowerCase() + "\""));
    }

    @Test
    public void deserializeFromLowercase() throws Exception {
        ObjectMapper mapper = ZenDesk.createMapper();
        ObjectReader reader = mapper.reader(Status.class);
        assertThat(reader.readValue("\"" + Status.PENDING.name().toLowerCase() + "\""), is((Object)Status.PENDING));
    }
}
