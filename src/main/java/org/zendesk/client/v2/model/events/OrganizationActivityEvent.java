package org.zendesk.client.v2.model.events;

import java.util.List;

import org.zendesk.client.v2.model.Via;

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
      final StringBuilder sb = new StringBuilder();
      sb.append("OrganizationActivityEvent");
      sb.append("{subject=").append(subject);
      sb.append(", body=").append(body);
      sb.append(", recipients=").append(recipients);
      sb.append(", via=").append(via);
      sb.append('}');
      return sb.toString();
  }
}
