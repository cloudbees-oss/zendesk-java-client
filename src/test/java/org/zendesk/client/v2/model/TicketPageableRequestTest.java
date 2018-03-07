package org.zendesk.client.v2.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.zendesk.client.v2.model.request.page.PageableRequest;
import org.zendesk.client.v2.model.request.page.TicketPageableRequest;
import org.zendesk.client.v2.model.sort.Sort;
import org.zendesk.client.v2.model.sort.SortOrder;
import org.zendesk.client.v2.model.sort.ticket.TicketSort;
import org.zendesk.client.v2.model.sort.ticket.TicketSortableField;

public class TicketPageableRequestTest {

	@Test
	public void testTicketPageableRequestTicketSort() {
		TicketPageableRequest req = new TicketPageableRequest(null);
		assertNotNull(req);
		assertEquals(0, req.getPage());
		assertEquals(0, req.getPerPage());
		assertEquals(null, req.getSort());
		assertEquals(0, req.getQueryParameters().size());

		TicketSort sort = new TicketSort(TicketSortableField.ID, SortOrder.ASC);
		req = new TicketPageableRequest(sort);
		assertNotNull(req);
		assertEquals(0, req.getPage());
		assertEquals(0, req.getPerPage());
		assertEquals(sort, req.getSort());
		Map<String, String> map = new HashMap<String, String>();
		map.put(Sort.SORT_BY, "id");
		map.put(Sort.SORT_ORDER, "asc");
		assertEquals(map, req.getQueryParameters());
	}

	@Test
	public void testTicketPageableRequestIntIntTicketSort() {
		TicketSort sort = new TicketSort(TicketSortableField.ID, SortOrder.ASC);
		TicketPageableRequest req = new TicketPageableRequest(4, 5, sort);
		assertNotNull(req);
		assertEquals(4, req.getPage());
		assertEquals(5, req.getPerPage());
		assertEquals(sort, req.getSort());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(PageableRequest.PAGE, 4);
		map.put(PageableRequest.PER_PAGE, 5);
		map.put(Sort.SORT_BY, "id");
		map.put(Sort.SORT_ORDER, "asc");
		assertEquals(map, req.getQueryParameters());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTicketPageableRequestIntIntTicketSort_whenPageNegative_thenIllegalArgumentException() {
		TicketSort sort = new TicketSort(TicketSortableField.ID, SortOrder.ASC);
		new TicketPageableRequest(-4, 5, sort);
	}

}
