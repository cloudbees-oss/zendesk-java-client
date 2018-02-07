package org.zendesk.client.v2.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Sandeep Kaul(sandeep.kaul@olacabs.com)
 *
 */
public class Conditions implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<Condition> all = new ArrayList<>();
  private List<Condition> any = new ArrayList<>();

  public List<Condition> getAll() {
    return all;
  }

  public void setAll(List<Condition> all) {
    this.all = all;
  }

  public List<Condition> getAny() {
    return any;
  }

  public void setAny(List<Condition> any) {
    this.any = any;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Conditions");
    sb.append("{all=").append(all);
    sb.append(", any=").append(any);
    sb.append('}');
    return sb.toString();
  }

}
