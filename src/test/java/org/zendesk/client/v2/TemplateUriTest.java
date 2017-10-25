package org.zendesk.client.v2;


import com.damnhandy.uri.template.UriTemplate;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TemplateUriTest {

    @Test
    public void testToString_whenUriTempateConstructor_thenUriBuilt() {
        UriTemplate uriTemplate = UriTemplate.fromTemplate("/{foo:1}{/foo}");
        TemplateUri templateUri = new TemplateUri(uriTemplate);
        templateUri.set("foo", "test");

        assertEquals("/t/test", templateUri.toString());
    }

    @Test
    public void testToString_whenStringConstructor_thenUriBuilt() {
        TemplateUri templateUri = new TemplateUri("/{foo:1}{/foo}");
        templateUri.set("foo", "test");

        assertEquals("/t/test", templateUri.toString());
    }

    @Test
    public void testToString_whenMapValues_thenUriBuilt() {
        TemplateUri templateUri = new TemplateUri("/{foo:1}{/foo}");
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("foo", "test");

        templateUri.set(properties);

        assertEquals("/t/test", templateUri.toString());
    }

    @Test
    public void testToString_whenDateValue_thenUriBuilt() throws ParseException {
        TemplateUri templateUri = new TemplateUri("/test?date={foo}");

        Date date = new SimpleDateFormat("mm/dd/yyyy").parse("1/1/2017");
        templateUri.set("foo", date);

        assertEquals("/test?date=2017-01-01T00%3A01%3A00.000-0600", templateUri.toString());
    }
}
