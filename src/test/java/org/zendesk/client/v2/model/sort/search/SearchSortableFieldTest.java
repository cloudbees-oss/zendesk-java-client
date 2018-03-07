package org.zendesk.client.v2.model.sort.search;

import static org.junit.Assert.*;

import org.junit.Test;

public class SearchSortableFieldTest {

	@Test
	public void testGetKeyword() {
		assertEquals("created_at", SearchSortableField.CREATED_AT.getKeyword());
		assertEquals("priority", SearchSortableField.PRIORITY.getKeyword());
		assertEquals("status", SearchSortableField.STATUS.getKeyword());
		assertEquals("ticket_type", SearchSortableField.TICKET_TYPE.getKeyword());
		assertEquals("updated_at", SearchSortableField.UPDATED_AT.getKeyword());
	}

	@Test
	public void testGetByKeyword() {
		assertEquals(SearchSortableField.CREATED_AT, SearchSortableField.getByKeyword("created_at"));
		assertEquals(SearchSortableField.CREATED_AT, SearchSortableField.getByKeyword("created_AT"));
		assertEquals(SearchSortableField.CREATED_AT, SearchSortableField.getByKeyword("CREATED_AT"));

		assertEquals(SearchSortableField.PRIORITY, SearchSortableField.getByKeyword("priority"));
		assertEquals(SearchSortableField.PRIORITY, SearchSortableField.getByKeyword("PRIORITY"));
		assertEquals(SearchSortableField.PRIORITY, SearchSortableField.getByKeyword("priORity"));

		assertEquals(SearchSortableField.STATUS, SearchSortableField.getByKeyword("status"));
		assertEquals(SearchSortableField.STATUS, SearchSortableField.getByKeyword("staTUs"));
		assertEquals(SearchSortableField.STATUS, SearchSortableField.getByKeyword("STATUS"));

		assertEquals(SearchSortableField.TICKET_TYPE, SearchSortableField.getByKeyword("ticket_type"));
		assertEquals(SearchSortableField.TICKET_TYPE, SearchSortableField.getByKeyword("tickEt_Type"));
		assertEquals(SearchSortableField.TICKET_TYPE, SearchSortableField.getByKeyword("TICKET_TYPE"));

		assertEquals(SearchSortableField.UPDATED_AT, SearchSortableField.getByKeyword("updated_at"));
		assertEquals(SearchSortableField.UPDATED_AT, SearchSortableField.getByKeyword("updated_AT"));
		assertEquals(SearchSortableField.UPDATED_AT, SearchSortableField.getByKeyword("UPDATED_AT"));

		assertNull(SearchSortableField.getByKeyword("abc"));
		assertNull(SearchSortableField.getByKeyword(null));
	}

}
