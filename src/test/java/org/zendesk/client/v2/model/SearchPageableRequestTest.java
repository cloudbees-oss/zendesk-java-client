package org.zendesk.client.v2.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.zendesk.client.v2.model.request.page.PageableRequest;
import org.zendesk.client.v2.model.request.page.SearchPageableRequest;
import org.zendesk.client.v2.model.sort.Sort;
import org.zendesk.client.v2.model.sort.SortOrder;
import org.zendesk.client.v2.model.sort.search.SearchSort;
import org.zendesk.client.v2.model.sort.search.SearchSortableField;

public class SearchPageableRequestTest {

	@Test
	public void testSearchPageableRequestSearchSort() {
		SearchPageableRequest req = new SearchPageableRequest(null);
		assertNotNull(req);
		assertEquals(0, req.getPage());
		assertEquals(0, req.getPerPage());
		assertEquals(null, req.getSort());
		assertEquals(0, req.getQueryParameters().size());

		SearchSort sort = new SearchSort(SearchSortableField.STATUS, SortOrder.ASC);
		req = new SearchPageableRequest(sort);
		assertNotNull(req);
		assertEquals(0, req.getPage());
		assertEquals(0, req.getPerPage());
		assertEquals(sort, req.getSort());
		Map<String, String> map = new HashMap<String, String>();
		map.put(Sort.SORT_BY, "status");
		map.put(Sort.SORT_ORDER, "asc");
		assertEquals(map, req.getQueryParameters());
	}

	@Test
	public void testSearchPageableRequestIntIntSearchSort() {
		SearchSort sort = new SearchSort(SearchSortableField.STATUS, SortOrder.ASC);
		SearchPageableRequest req = new SearchPageableRequest(4, 5, sort);
		assertNotNull(req);
		assertEquals(4, req.getPage());
		assertEquals(5, req.getPerPage());
		assertEquals(sort, req.getSort());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(PageableRequest.PAGE, 4);
		map.put(PageableRequest.PER_PAGE, 5);
		map.put(Sort.SORT_BY, "status");
		map.put(Sort.SORT_ORDER, "asc");
		assertEquals(map, req.getQueryParameters());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSearchPageableRequestIntIntSearchSort_whenPageNegative_thenException() {
		SearchSort sort = new SearchSort(SearchSortableField.STATUS, SortOrder.ASC);
		new SearchPageableRequest(-4, 5, sort);
	}

}
