package org.zendesk.client.v2.search;

import static java.lang.String.format;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

import java.util.EnumSet;

public abstract class SearchableProperty {

    private String property;
    private EnumSet<Operator> supportedOperators;

    protected SearchableProperty(String property, Operator... supportedOperators) {
        this.property = property;
        this.supportedOperators = isNotEmpty(supportedOperators) ?
                EnumSet.of(supportedOperators[0], supportedOperators) :
                EnumSet.allOf(Operator.class);
    }

    public String getPropertyFor(Operator op) {
        if (!isSupported(op)) {
            throw new IllegalArgumentException(format("Property '%s' cannot be used with operator '%s'", property, op.name()));
        }

        return property;
    }

    public boolean isSupported(Operator op) {
        return supportedOperators.contains(op);
    }
}
