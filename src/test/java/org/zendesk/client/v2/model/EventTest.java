package org.zendesk.client.v2.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zendesk.client.v2.model.events.AgentMacroReferenceEvent;
import org.zendesk.client.v2.model.events.AttachmentRedactionEvent;
import org.zendesk.client.v2.model.events.CommentRedactionEvent;
import org.zendesk.client.v2.model.events.Event;
import org.zendesk.client.v2.model.events.OrganizationActivityEvent;
import org.zendesk.client.v2.model.events.UnknownEvent;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EventTest {

  private Event parseJson( byte[] json ) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue( json, Event.class );
    } catch ( Exception e ) {
      e.printStackTrace();
      return null;
    }
  }

  @Test
  public void testOrganizationActivityEvent() {
    String json = "{ \"id\": 21337631753, \"type\": \"OrganizationActivity\", \"subject\": \"Custom Subject\", \"body\": \"This is sample data\", \"recipients\": [568628833] }";
    Event ev = parseJson( json.getBytes() );
    assertNotNull( ev );
    assertEquals( OrganizationActivityEvent.class, ev.getClass() );
    assertEquals( new Long(21337631753L), ev.getId() );
    assertEquals( "Custom Subject", ((OrganizationActivityEvent) ev).getSubject() );
    assertEquals( "This is sample data", ((OrganizationActivityEvent) ev).getBody() );
    assertNotNull( ((OrganizationActivityEvent) ev).getRecipients() );
    assertEquals( 1, ((OrganizationActivityEvent) ev).getRecipients().size() );
    assertEquals( new Long(568628833L), ((OrganizationActivityEvent) ev).getRecipients().get(0) );
    assertEquals( "OrganizationActivityEvent{subject=Custom Subject, body=This is sample data, recipients=[568628833], via=null}", ev.toString() );
  }

  @Test
  public void testAttachmentRedactionEvent() {
    String json = "{ \"id\": 10593649089, \"type\": \"AttachmentRedactionEvent\", \"attachment_id\": 315988189, \"comment_id\": 10591294149 }";
    Event ev = parseJson( json.getBytes() );
    assertNotNull( ev );
    assertEquals( AttachmentRedactionEvent.class, ev.getClass() );
    assertEquals( new Long(10593649089L), ev.getId() );
    assertEquals( new Long(315988189L), ((AttachmentRedactionEvent) ev).getAttachmentId() );
    assertEquals( new Long(10591294149L), ((AttachmentRedactionEvent) ev).getCommentId() );
    assertEquals( "AttachmentRedactionEvent{attachmentId=315988189, commentId=10591294149}", ev.toString() );
  }

  @Test
  public void testCommentRedactionEvent() {
    String json = "{ \"id\": 18231937759, \"type\": \"CommentRedactionEvent\", \"comment_id\": \"18974155255\" }";
    Event ev = parseJson( json.getBytes() );
    assertNotNull( ev );
    assertEquals( CommentRedactionEvent.class, ev.getClass() );
    assertEquals( new Long(18231937759L), ev.getId() );
    assertEquals( new Long(18974155255L), ((CommentRedactionEvent) ev).getCommentId() );
    assertEquals( "CommentRedactionEvent{commentId=18974155255}", ev.toString() );
  }

  @Test
  public void testAgentMacroReferenceEvent() {
    String json = "{ \"id\": 789, \"type\": \"AgentMacroReference\", \"via\": { \"channel\": \"web\", \"source\": {"
        + "\"from\": {}, \"to\": {}, \"rel\": null } }, \"macro_title\": \"TheMacroTitle\", \"macro_id\": \"123\" }";
    Event ev = parseJson( json.getBytes() );
    assertNotNull(ev);
    assertEquals(AgentMacroReferenceEvent.class, ev.getClass());
    assertEquals(new Long(789L), ev.getId());
    assertEquals(new Long(123L), ((AgentMacroReferenceEvent) ev).getMacroId());
    assertEquals("TheMacroTitle", ((AgentMacroReferenceEvent) ev).getMacroTitle());
    assertNotNull(((AgentMacroReferenceEvent) ev).getVia());
  }

  @Test
  public void testUnknownEvent() {
    String json = "{ \"id\": 123, \"type\": \"NotARealEventType\" }";
    Event ev = parseJson(json.getBytes());
    assertNotNull(ev);
    assertEquals(UnknownEvent.class, ev.getClass());
    assertEquals("NotARealEventType", ((UnknownEvent) ev).getType());
    assertTrue(ev.toString().contains("NotARealEventType"));
  }
}
