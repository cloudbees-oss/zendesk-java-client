package org.zendesk.client.v2.model.sort.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.zendesk.client.v2.model.sort.Sort;
import org.zendesk.client.v2.model.sort.SortOrder;

public class SearchSortTest {

	@Test(expected = IllegalArgumentException.class)
	public void testSearchSort_whenFieldAndOrderAreNull_thenIllegalArgumentException() {
		new SearchSort(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSearchSort_whenOrderNull_thenIllegalArgumentException() {
		new SearchSort(SearchSortableField.CREATED_AT, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSearchSort_whenFieldNull_thenIllegalArgumentException() {
		new SearchSort(null, SortOrder.ASC);
	}

	public void testSearchSort_whenFieldAndOrderNotNull_thenObjectCreated() {
		SearchSort sort = new SearchSort(SearchSortableField.CREATED_AT, SortOrder.ASC);
		assertNotNull(sort);
		assertEquals(SearchSortableField.CREATED_AT, sort.getField());
		assertEquals(SortOrder.ASC, sort.getOrder());
	}

	@Test
	public void testToUrl() {
		SearchSort sort = new SearchSort(SearchSortableField.UPDATED_AT, SortOrder.ASC);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Sort.SORT_BY, "updated_at");
		map.put(Sort.SORT_ORDER, "asc");
		assertEquals(map, sort.getQueryParameters());
		sort = new SearchSort(SearchSortableField.PRIORITY, SortOrder.DESC);
		map = new HashMap<String, Object>();
		map.put(Sort.SORT_BY, "priority");
		map.put(Sort.SORT_ORDER, "desc");
		assertEquals(map, sort.getQueryParameters());
	}

}
