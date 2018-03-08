package org.zendesk.client.v2.model.search;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zendesk.client.v2.model.Group;
import org.zendesk.client.v2.model.Organization;
import org.zendesk.client.v2.model.Ticket;
import org.zendesk.client.v2.model.User;

public class SearchResultTypeTest {

	@Test
	public void testGetName() {
		assertEquals("group", SearchResultType.GROUP.getName());
		assertEquals("organization", SearchResultType.ORGANIZATION.getName());
		assertEquals("ticket", SearchResultType.TICKET.getName());
		assertEquals("user", SearchResultType.USER.getName());
	}

	@Test
	public void testGetTypeClass() {
		assertEquals(Group.class, SearchResultType.GROUP.getTypeClass());
		assertEquals(Organization.class, SearchResultType.ORGANIZATION.getTypeClass());
		assertEquals(Ticket.class, SearchResultType.TICKET.getTypeClass());
		assertEquals(User.class, SearchResultType.USER.getTypeClass());
	}

}
