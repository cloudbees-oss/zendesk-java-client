package org.zendesk.client.v2.model;

/**
 * @author stephenc
 * @since 05/04/2013 08:57
 */
public enum Priority {
  URGENT,
  HIGH,
  NORMAL,
  LOW;

  @Override
  public String toString() {
    return super.toString().toLowerCase();
  }
}
