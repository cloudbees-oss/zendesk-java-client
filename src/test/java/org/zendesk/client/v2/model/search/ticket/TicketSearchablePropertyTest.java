package org.zendesk.client.v2.model.search.ticket;

import static org.junit.Assert.*;

import org.junit.Test;

public class TicketSearchablePropertyTest {

	@Test
	public void testGetKeyword() {
		assertEquals("assignee", TicketSearchableProperty.ASSIGNEE.getKeyword());
		assertEquals("brand", TicketSearchableProperty.BRAND.getKeyword());
		assertEquals("cc", TicketSearchableProperty.CC.getKeyword());
		assertEquals("commenter", TicketSearchableProperty.COMMENTER.getKeyword());
		assertEquals("created", TicketSearchableProperty.CREATED.getKeyword());
		assertEquals("fieldvalue", TicketSearchableProperty.CUSTOM_FIELD.getKeyword());
		assertEquals("description", TicketSearchableProperty.DESCRIPTION.getKeyword());
		assertEquals("due_date", TicketSearchableProperty.DUE_DATE.getKeyword());
		assertEquals("group", TicketSearchableProperty.GROUP.getKeyword());
		assertEquals("", TicketSearchableProperty.ID.getKeyword());
		assertEquals("organization", TicketSearchableProperty.ORGANIZATION.getKeyword());
		assertEquals("priority", TicketSearchableProperty.PRIORITY.getKeyword());
		assertEquals("requester", TicketSearchableProperty.REQUESTER.getKeyword());
		assertEquals("solved", TicketSearchableProperty.SOLVED.getKeyword());
		assertEquals("status", TicketSearchableProperty.STATUS.getKeyword());
		assertEquals("subject", TicketSearchableProperty.SUBJECT.getKeyword());
		assertEquals("submitter", TicketSearchableProperty.SUBMITTER.getKeyword());
		assertEquals("tags", TicketSearchableProperty.TAGS.getKeyword());
		assertEquals("ticket_type", TicketSearchableProperty.TICKET_TYPE.getKeyword());
		assertEquals("updated", TicketSearchableProperty.UPDATED.getKeyword());
		assertEquals("via", TicketSearchableProperty.VIA.getKeyword());
	}

	@Test
	public void testGetByKeyword() {
		assertEquals(TicketSearchableProperty.ASSIGNEE, TicketSearchableProperty.getByKeyword("assignee"));
		assertEquals(TicketSearchableProperty.ASSIGNEE, TicketSearchableProperty.getByKeyword("AssigneE"));
		assertEquals(TicketSearchableProperty.BRAND, TicketSearchableProperty.getByKeyword("brand"));
		assertEquals(TicketSearchableProperty.BRAND, TicketSearchableProperty.getByKeyword("branD"));
		assertEquals(TicketSearchableProperty.CC, TicketSearchableProperty.getByKeyword("cc"));
		assertEquals(TicketSearchableProperty.CC, TicketSearchableProperty.getByKeyword("CC"));
		assertEquals(TicketSearchableProperty.COMMENTER, TicketSearchableProperty.getByKeyword("commenter"));
		assertEquals(TicketSearchableProperty.COMMENTER, TicketSearchableProperty.getByKeyword("commenteR"));
		assertEquals(TicketSearchableProperty.CREATED, TicketSearchableProperty.getByKeyword("created"));
		assertEquals(TicketSearchableProperty.CREATED, TicketSearchableProperty.getByKeyword("createD"));
		assertEquals(TicketSearchableProperty.CUSTOM_FIELD, TicketSearchableProperty.getByKeyword("fieldvalue"));
		assertEquals(TicketSearchableProperty.CUSTOM_FIELD, TicketSearchableProperty.getByKeyword("fieldvaluE"));
		assertEquals(TicketSearchableProperty.DESCRIPTION, TicketSearchableProperty.getByKeyword("description"));
		assertEquals(TicketSearchableProperty.DESCRIPTION, TicketSearchableProperty.getByKeyword("descriptioN"));
		assertEquals(TicketSearchableProperty.DUE_DATE, TicketSearchableProperty.getByKeyword("due_date"));
		assertEquals(TicketSearchableProperty.DUE_DATE, TicketSearchableProperty.getByKeyword("due_datE"));
		assertEquals(TicketSearchableProperty.GROUP, TicketSearchableProperty.getByKeyword("group"));
		assertEquals(TicketSearchableProperty.GROUP, TicketSearchableProperty.getByKeyword("grouP"));
		assertEquals(TicketSearchableProperty.ID, TicketSearchableProperty.getByKeyword(""));
		assertEquals(TicketSearchableProperty.ID, TicketSearchableProperty.getByKeyword("  "));
		assertEquals(TicketSearchableProperty.ORGANIZATION, TicketSearchableProperty.getByKeyword("organization"));
		assertEquals(TicketSearchableProperty.ORGANIZATION, TicketSearchableProperty.getByKeyword("organizatioN "));
		assertEquals(TicketSearchableProperty.PRIORITY, TicketSearchableProperty.getByKeyword("priority"));
		assertEquals(TicketSearchableProperty.PRIORITY, TicketSearchableProperty.getByKeyword(" prioritY "));
		assertEquals(TicketSearchableProperty.REQUESTER, TicketSearchableProperty.getByKeyword("requester"));
		assertEquals(TicketSearchableProperty.REQUESTER, TicketSearchableProperty.getByKeyword(" REQUESTER "));
		assertEquals(TicketSearchableProperty.SOLVED, TicketSearchableProperty.getByKeyword("solved"));
		assertEquals(TicketSearchableProperty.SOLVED, TicketSearchableProperty.getByKeyword("solveD"));
		assertEquals(TicketSearchableProperty.STATUS, TicketSearchableProperty.getByKeyword("status"));
		assertEquals(TicketSearchableProperty.STATUS, TicketSearchableProperty.getByKeyword("statuS"));
		assertEquals(TicketSearchableProperty.SUBJECT, TicketSearchableProperty.getByKeyword("subject"));
		assertEquals(TicketSearchableProperty.SUBJECT, TicketSearchableProperty.getByKeyword("subjecT"));
		assertEquals(TicketSearchableProperty.SUBMITTER, TicketSearchableProperty.getByKeyword("submitter"));
		assertEquals(TicketSearchableProperty.SUBMITTER, TicketSearchableProperty.getByKeyword("submitter   "));
		assertEquals(TicketSearchableProperty.TAGS, TicketSearchableProperty.getByKeyword("tags"));
		assertEquals(TicketSearchableProperty.TAGS, TicketSearchableProperty.getByKeyword("taGS  "));
		assertEquals(TicketSearchableProperty.TICKET_TYPE, TicketSearchableProperty.getByKeyword("ticket_type"));
		assertEquals(TicketSearchableProperty.TICKET_TYPE, TicketSearchableProperty.getByKeyword("   ticket_type"));
		assertEquals(TicketSearchableProperty.UPDATED, TicketSearchableProperty.getByKeyword("updatED  "));
		assertEquals(TicketSearchableProperty.UPDATED, TicketSearchableProperty.getByKeyword("updated"));
		assertEquals(TicketSearchableProperty.VIA, TicketSearchableProperty.getByKeyword("via"));
		assertEquals(TicketSearchableProperty.VIA, TicketSearchableProperty.getByKeyword("VIA"));
		
		assertNull(TicketSearchableProperty.getByKeyword(null));
	}

}
