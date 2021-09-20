package org.zendesk.client.v2.model;


public class TicketPage extends Page<Ticket> {

  private static final long serialVersionUID = 434807064715979598L;
  
  @Override
  public Class<Ticket> getTargetClass() {
    return Ticket.class;
  }
}
