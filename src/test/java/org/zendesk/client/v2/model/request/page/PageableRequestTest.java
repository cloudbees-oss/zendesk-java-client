package org.zendesk.client.v2.model.request.page;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.zendesk.client.v2.model.request.page.PageableRequest;
import org.zendesk.client.v2.model.sort.Sort;
import org.zendesk.client.v2.model.sort.SortOrder;
import org.zendesk.client.v2.model.sort.search.SearchSortableField;

public class PageableRequestTest {

	@Test
	public void testPageableRequestSort() {
		PageableRequest req = new PageableRequest(null) {
		};
		assertNotNull(req);
		assertEquals(0, req.getPage());
		assertEquals(0, req.getPerPage());
		assertNull(req.getSort());

		Sort sort = new Sort(SearchSortableField.CREATED_AT, SortOrder.ASC);
		req = new PageableRequest(sort) {
		};
		assertNotNull(req);
		assertEquals(0, req.getPage());
		assertEquals(0, req.getPerPage());
		assertNotNull(req.getSort());
	}

	@Test
	public void testPageableRequestIntIntSort() {
		PageableRequest req = new PageableRequest(1, 1, null) {
		};
		assertNotNull(req);
		assertEquals(1, req.getPage());
		assertEquals(1, req.getPerPage());
		assertNull(req.getSort());

		Sort sort = new Sort(SearchSortableField.CREATED_AT, SortOrder.ASC);
		req = new PageableRequest(2, 3, sort) {
		};
		assertNotNull(req);
		assertEquals(2, req.getPage());
		assertEquals(3, req.getPerPage());
		assertNotNull(req.getSort());
	}

	@Test
	public void testPageableRequestIntInt() {
		PageableRequest req = new PageableRequest(3, 5) {
		};
		assertNotNull(req);
		assertEquals(3, req.getPage());
		assertEquals(5, req.getPerPage());
		assertNull(req.getSort());
	}

	@Test
	public void testToUrl() {
		PageableRequest req = new PageableRequest(3, 5) {
		};
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(PageableRequest.PAGE, 3);
		map.put(PageableRequest.PER_PAGE, 5);
		assertEquals(map, req.getQueryParameters());

		Sort sort = new Sort(SearchSortableField.CREATED_AT, SortOrder.ASC);
		req = new PageableRequest(2, 3, sort) {
		};
		map = new HashMap<String, Object>();
		map.put(PageableRequest.PAGE, 2);
		map.put(PageableRequest.PER_PAGE, 3);
		map.put(Sort.SORT_BY, "created_at");
		map.put(Sort.SORT_ORDER, "asc");
		assertEquals(map, req.getQueryParameters());

		req = new PageableRequest(null) {
		};
		assertEquals(new HashMap<String, Object>(), req.getQueryParameters());

		req = new PageableRequest(sort) {
		};
		map = new HashMap<String, Object>();
		map.put(Sort.SORT_BY, "created_at");
		map.put(Sort.SORT_ORDER, "asc");
		assertEquals(map, req.getQueryParameters());

		req = new PageableRequest(3, 0) {
		};
		map = new HashMap<String, Object>();
		map.put(PageableRequest.PAGE, 3);
		assertEquals(map, req.getQueryParameters());
	}

}
