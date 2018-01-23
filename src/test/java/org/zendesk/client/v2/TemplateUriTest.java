package org.zendesk.client.v2;

import com.damnhandy.uri.template.UriTemplate;
import org.junit.Rule;
import org.junit.Test;
import org.zendesk.client.v2.junit.UTCRule;

import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class TemplateUriTest {

    @Rule
    public UTCRule utcRule = new UTCRule();

    @Test
    public void testUriTempateConstructor() {
        UriTemplate uriTemplate = UriTemplate.fromTemplate("/{foo:1}{/foo}");
        TemplateUri templateUri = new TemplateUri(uriTemplate);
        templateUri.set("foo", "test");

        assertEquals("/t/test", templateUri.toString());
    }

    @Test
    public void testStringConstructor() {
        TemplateUri templateUri = new TemplateUri("/{foo:1}{/foo}");
        templateUri.set("foo", "test");

        assertEquals("/t/test", templateUri.toString());
    }

    @Test
    public void testMapValues() {
        TemplateUri templateUri = new TemplateUri("/{foo:1}{/foo}");
        Map<String, Object> properties = new HashMap<>();
        properties.put("foo", "test");

        templateUri.set(properties);

        assertEquals("/t/test", templateUri.toString());
    }

    @Test
    public void testDateValue() throws ParseException {
        TemplateUri templateUri = new TemplateUri("/test?date={foo}");

        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.YEAR, 2018);
        cal.set(Calendar.MONTH, Calendar.APRIL);
        cal.set(Calendar.DAY_OF_MONTH, 20);
        cal.set(Calendar.HOUR_OF_DAY, 16);
        cal.set(Calendar.MINUTE, 20);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        templateUri.set("foo", cal.getTime());

        assertEquals("/test?date=2018-04-20T16%3A20%3A00.000%2B0000", templateUri.toString());
    }
}
