package org.zendesk.client.v2.model.search;

import static org.junit.Assert.*;

import org.junit.Test;
import org.zendesk.client.v2.model.Group;
import org.zendesk.client.v2.model.Organization;
import org.zendesk.client.v2.model.Ticket;
import org.zendesk.client.v2.model.Topic;
import org.zendesk.client.v2.model.User;
import org.zendesk.client.v2.model.hc.Article;

public class SearchResultTypeTest {

	@Test
	public void testGetName() {
		assertEquals("article", SearchResultType.ARTICLE.getName());
		assertEquals("group", SearchResultType.GROUP.getName());
		assertEquals("organization", SearchResultType.ORGANIZATION.getName());
		assertEquals("ticket", SearchResultType.TICKET.getName());
		assertEquals("topic", SearchResultType.TOPIC.getName());
		assertEquals("user", SearchResultType.USER.getName());
	}

	@Test
	public void testGetTypeClass() {
		assertEquals(Article.class, SearchResultType.ARTICLE.getTypeClass());
		assertEquals(Group.class, SearchResultType.GROUP.getTypeClass());
		assertEquals(Organization.class, SearchResultType.ORGANIZATION.getTypeClass());
		assertEquals(Ticket.class, SearchResultType.TICKET.getTypeClass());
		assertEquals(Topic.class, SearchResultType.TOPIC.getTypeClass());
		assertEquals(User.class, SearchResultType.USER.getTypeClass());
	}

}
