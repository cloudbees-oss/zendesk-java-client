package org.zendesk.client.v2.search;

import static java.util.Arrays.stream;
import static org.zendesk.client.v2.search.Operator.AND;
import static org.zendesk.client.v2.search.Operator.AS_IS;
import static org.zendesk.client.v2.search.Operator.EQ;
import static org.zendesk.client.v2.search.Operator.NOT;

public class QueryCondition {

    private Operator op;
    private String leftOperand;
    private String rightOperand;

    private QueryCondition(Operator op, String leftOperand) {
        this.op = op;
        this.leftOperand = leftOperand;
    }

    private QueryCondition(Operator op, String leftOperand, String rightOperand) {
        this.op = op;
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    public String apply() {
        return op.apply(leftOperand, rightOperand);
    }

    public static QueryCondition not(QueryCondition op1) {
        return new QueryCondition(NOT, op1.apply());
    }

    public static QueryCondition and(QueryCondition condition, QueryCondition... conditions) {
        return stream(conditions).reduce(condition, (op1, op2) -> new QueryCondition(AND, op1.apply(), op2.apply()));
    }

    public static QueryCondition eq(SearchableProperty property, String value) {
        return new QueryCondition(EQ, property.getPropertyFor(EQ), value);
    }

    public static QueryCondition asIs(SearchableProperty property, String value) {

        String ignored = property.getPropertyFor(AS_IS); // method checks that property supports AS_IS operation

        return new QueryCondition(AS_IS, value);
    }
}
