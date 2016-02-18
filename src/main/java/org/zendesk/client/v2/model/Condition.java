/**
 * 
 */
package org.zendesk.client.v2.model;

/**
 * @author Sandeep Kaul (sandeep.kaul@olacabs.com)
 *
 */
public class Condition {
  private String field;
  private String operator;
  private String value;

  public Condition() {}

  public Condition(String field, String operator, String value) {
    this.field = field;
    this.operator = operator;
    this.value = value;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Condition");
    sb.append("{field=").append(field);
    sb.append(", operator=").append(operator);
    sb.append(", value=").append(value);
    sb.append('}');
    return sb.toString();
  }

}
