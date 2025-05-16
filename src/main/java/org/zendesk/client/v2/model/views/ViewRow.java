package org.zendesk.client.v2.model.views;

import java.io.Serializable;
import java.util.Objects;
import org.zendesk.client.v2.model.Ticket;

public abstract class ViewRow implements Serializable {
  private static final long serialVersionUID = -5436397225795525024L;
  private Ticket ticket;

  public Ticket getTicket() {
    return ticket;
  }

  public void setTicket(Ticket ticket) {
    this.ticket = ticket;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;

    ViewRow viewRow = (ViewRow) o;
    return Objects.equals(ticket, viewRow.ticket);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(ticket);
  }

  @Override
  public String toString() {
    return "ViewRow{" + "ticket=" + ticket + '}';
  }
}
