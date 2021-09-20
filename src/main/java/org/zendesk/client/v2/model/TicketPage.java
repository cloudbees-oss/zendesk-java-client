package org.zendesk.client.v2.model;

import java.util.List;

public class TicketPage extends Page<Ticket> {

  private static final long serialVersionUID = 434807064715979598L;
  
  private List<Ticket> results;

  public List<Ticket> getResults() {
    return results;
  }

  public void setResults(final List<Ticket> results) {
    this.results = results;
  }

  @Override
  public Class<Ticket> getTargetClass() {
    return Ticket.class;
  }
}
