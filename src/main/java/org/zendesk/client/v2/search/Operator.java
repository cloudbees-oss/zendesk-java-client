package org.zendesk.client.v2.search;

import static java.util.function.Function.identity;
import static org.apache.commons.lang3.StringUtils.containsWhitespace;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;

enum Operator {

    /** The colon indicates that the given field should equal the specified value. */
    EQ((a, b) -> a + ":" + b),
    /** Less than. */
    L((a, b) -> a + "<" + b),
    /** Greater than. */
    G((a, b) -> a + ">" + b),
    /** Less than or equal to. */
    LEQ((a, b) -> a + "<=" + b),
    /** Greater than or equal to. */
    GEQ((a, b) -> a + ">=" + b),
    /** Minus sign. Excludes items containing a word (or property value) from the search results */
    NOT((a) -> "-" + a),
    /** Concatenate search criteria. */
    AND((a, b) -> a + "+" + b),
    AS_IS(identity()),
    QUOTE((a) -> "\"" + a + "\"")
    ;

    private Function<String, String> function;
    private BiFunction<String, String, String> biFunction;

    Operator(Function<String, String> function) {
        this.function = function;

    }

    Operator(BiFunction<String, String, String> biFunction) {
        this.biFunction = biFunction;
    }

    public String apply(String leftOperand, String... rightOperands) {

        if (isBlank(leftOperand)) {
            throw new IllegalArgumentException("Operator should take at least one argument");
        }

        if (ArrayUtils.isEmpty(rightOperands) || isBlank(rightOperands[0])) {
            return Optional.ofNullable(function).map(f -> f.apply(leftOperand))
                    .orElseThrow(() -> new IllegalArgumentException("Operator takes more than one argument."));
        }

        String rightOperand = containsWhitespace(rightOperands[0]) ? QUOTE.apply(rightOperands[0]) : rightOperands[0];

        return Optional.ofNullable(biFunction).map(f -> f.apply(leftOperand, rightOperand))
                .orElseThrow(() -> new IllegalArgumentException("Operator takes only one argument."));
    }
}
