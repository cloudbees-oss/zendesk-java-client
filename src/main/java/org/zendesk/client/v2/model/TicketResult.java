package org.zendesk.client.v2.model;

public class TicketResult {
    private Ticket ticket;

    public TicketResult() {
    }

    public TicketResult(Ticket ticket) {
        this.ticket = ticket;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}
