package org.zendesk.client.v2.model.sort.ticket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.zendesk.client.v2.model.sort.Sort;
import org.zendesk.client.v2.model.sort.SortOrder;

public class TicketSortTest {

	@Test(expected = IllegalArgumentException.class)
	public void testTicketSort_whenFieldAndOrderAreNull_thenIllegalArgumentException() {
		new TicketSort(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTicketSort_whenOrderNull_thenIllegalArgumentException() {
		new TicketSort(TicketSortableField.CREATED_AT, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTicketSort_whenFieldNull_thenIllegalArgumentException() {
		new TicketSort(null, SortOrder.ASC);
	}

	public void testTicketSort_whenFieldAndOrderNotNull_thenObjectCreated() {
		TicketSort sort = new TicketSort(TicketSortableField.CREATED_AT, SortOrder.ASC);
		assertNotNull(sort);
		assertEquals(TicketSortableField.CREATED_AT, sort.getField());
		assertEquals(SortOrder.ASC, sort.getOrder());
	}

	@Test
	public void testToUrl() {
		TicketSort sort = new TicketSort(TicketSortableField.CREATED_AT, SortOrder.ASC);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Sort.SORT_BY, "created_at");
		map.put(Sort.SORT_ORDER, "asc");
		assertEquals(map, sort.getQueryParameters());
		sort = new TicketSort(TicketSortableField.ASSIGNEE, SortOrder.DESC);
		map = new HashMap<String, Object>();
		map.put(Sort.SORT_BY, "assignee");
		map.put(Sort.SORT_ORDER, "desc");
		assertEquals(map, sort.getQueryParameters());
	}
}
