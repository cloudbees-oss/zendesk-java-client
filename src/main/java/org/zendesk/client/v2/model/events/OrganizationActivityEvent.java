package org.zendesk.client.v2.model.events;

import org.zendesk.client.v2.model.Via;

import java.util.List;

/**
 * A notification was sent to the organization subscribers
 * 
 * @author matthewtckr
 * @see <a href="https://developer.zendesk.com/rest_api/docs/core/ticket_audits#organization-subscription-notification-event">Zendesk API Documentation</a>
 *
 */
public class OrganizationActivityEvent extends Event {

  private static final long serialVersionUID = 1L;

  private String subject;
  private String body;
  private List<Long> recipients;
  private Via via;

  public String getSubject() {
    return subject;
  }

  public void setSubject( String subject ) {
    this.subject = subject;
  }

  public String getBody() {
    return body;
  }

  public void setBody( String body ) {
    this.body = body;
  }

  public List<Long> getRecipients() {
    return recipients;
  }

  public void setRecipients( List<Long> recipients ) {
    this.recipients = recipients;
  }

  public Via getVia() {
    return via;
  }

  public void setVia( Via via ) {
    this.via = via;
  }

  @Override
  public String toString() {
      return "OrganizationActivityEvent" +
              "{subject=" + subject +
              ", body=" + body +
              ", recipients=" + recipients +
              ", via=" + via +
              '}';
  }
}
