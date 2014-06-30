package org.zendesk.client.v2.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.Test;
import org.zendesk.client.v2.Zendesk;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author stephenc
 * @since 05/04/2013 09:08
 */
public class StatusTest {
    @Test
    public void serializeAsLowercase() throws Exception {
        ObjectMapper mapper = Zendesk.createMapper();
        assertThat(mapper.writeValueAsString(Status.PENDING), is("\"" + Status.PENDING.name().toLowerCase() + "\""));
    }

    @Test
    public void deserializeFromLowercase() throws Exception {
        ObjectMapper mapper = Zendesk.createMapper();
        ObjectReader reader = mapper.reader(Status.class);
        assertThat(reader.readValue("\"" + Status.PENDING.name().toLowerCase() + "\""), is((Object)Status.PENDING));
    }
}
