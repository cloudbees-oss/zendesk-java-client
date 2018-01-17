package org.zendesk.client.v2.junit;

import org.joda.time.DateTimeZone;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
public class UTCRule extends TestWatcher {

    private DateTimeZone originalDefault = DateTimeZone.getDefault();

    @Override
    protected void starting(Description description) {
        DateTimeZone.setDefault(DateTimeZone.UTC);
    }

    @Override
    protected void finished(Description description) {
        DateTimeZone.setDefault(originalDefault);
    }
}
