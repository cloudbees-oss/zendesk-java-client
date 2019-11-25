package org.zendesk.client.v2.model.sort.ticket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class TicketSortableFieldTest {

	@Test
	public void testGetKeyword() {
		assertEquals("assignee", TicketSortableField.ASSIGNEE.getKeyword());
		assertEquals("assignee.name", TicketSortableField.ASSIGNEE_NAME.getKeyword());
		assertEquals("created_at", TicketSortableField.CREATED_AT.getKeyword());
		assertEquals("group", TicketSortableField.GROUP.getKeyword());
		assertEquals("id", TicketSortableField.ID.getKeyword());
		assertEquals("locale", TicketSortableField.LOCALE.getKeyword());
		assertEquals("requester", TicketSortableField.REQUESTER.getKeyword());
		assertEquals("requester.name", TicketSortableField.REQUESTER_NAME.getKeyword());
		assertEquals("status", TicketSortableField.STATUS.getKeyword());
		assertEquals("subject", TicketSortableField.SUBJECT.getKeyword());
		assertEquals("updated_at", TicketSortableField.UPDATED_AT.getKeyword());
	}

	@Test
	public void testGetByKeyword() {
		assertEquals(TicketSortableField.ASSIGNEE, TicketSortableField.getByKeyword("assignee"));
		assertEquals(TicketSortableField.ASSIGNEE, TicketSortableField.getByKeyword("assignEE"));

		assertEquals(TicketSortableField.ASSIGNEE_NAME, TicketSortableField.getByKeyword("assignee.name"));
		assertEquals(TicketSortableField.ASSIGNEE_NAME, TicketSortableField.getByKeyword("assignee.NAME"));

		assertEquals(TicketSortableField.CREATED_AT, TicketSortableField.getByKeyword("created_at"));
		assertEquals(TicketSortableField.CREATED_AT, TicketSortableField.getByKeyword("created_AT"));

		assertEquals(TicketSortableField.GROUP, TicketSortableField.getByKeyword("group"));
		assertEquals(TicketSortableField.GROUP, TicketSortableField.getByKeyword("GRoup"));

		assertEquals(TicketSortableField.ID, TicketSortableField.getByKeyword("id"));
		assertEquals(TicketSortableField.ID, TicketSortableField.getByKeyword("ID"));

		assertEquals(TicketSortableField.LOCALE, TicketSortableField.getByKeyword("locale"));
		assertEquals(TicketSortableField.LOCALE, TicketSortableField.getByKeyword("loCale"));

		assertEquals(TicketSortableField.REQUESTER, TicketSortableField.getByKeyword("requester"));
		assertEquals(TicketSortableField.REQUESTER, TicketSortableField.getByKeyword("requesTEr"));

		assertEquals(TicketSortableField.REQUESTER_NAME, TicketSortableField.getByKeyword("requester.name"));
		assertEquals(TicketSortableField.REQUESTER_NAME, TicketSortableField.getByKeyword("REQUESTER.name"));

		assertEquals(TicketSortableField.STATUS, TicketSortableField.getByKeyword("status"));
		assertEquals(TicketSortableField.STATUS, TicketSortableField.getByKeyword("statuS"));

		assertEquals(TicketSortableField.SUBJECT, TicketSortableField.getByKeyword("subject"));
		assertEquals(TicketSortableField.SUBJECT, TicketSortableField.getByKeyword("subjeCT"));

		assertEquals(TicketSortableField.UPDATED_AT, TicketSortableField.getByKeyword("updated_at"));
		assertEquals(TicketSortableField.UPDATED_AT, TicketSortableField.getByKeyword("UPDATED_at"));

		assertNull(TicketSortableField.getByKeyword("abc"));
		assertNull(TicketSortableField.getByKeyword(""));
		assertNull(TicketSortableField.getByKeyword("null"));
	}

}
