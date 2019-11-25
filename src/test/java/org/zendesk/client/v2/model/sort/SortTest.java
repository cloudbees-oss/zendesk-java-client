package org.zendesk.client.v2.model.sort;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.zendesk.client.v2.model.sort.search.SearchSortableField;

public class SortTest {

	@Test(expected=IllegalArgumentException.class)
	public void testSort_whenFieldAndOrderNull_thenIllegalArgumentException() {
		new Sort(null, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSort_whenOrderNull_thenIllegalArgumentException() {
		new Sort(SearchSortableField.CREATED_AT, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSort_whenFieldNull_thenIllegalArgumentException() {
		new Sort(null, SortOrder.ASC);
	}
	
	@Test
	public void testSort_whenFieldAndOrderNotNull_thenObjectCreated() {
		Sort sort = new Sort(SearchSortableField.CREATED_AT, SortOrder.ASC);
		assertNotNull(sort);
		assertEquals(SearchSortableField.CREATED_AT, sort.getField());
		assertEquals(SortOrder.ASC, sort.getOrder());
	}

	@Test
	public void testToUrl() {
		Sort sort = new Sort(SearchSortableField.CREATED_AT, SortOrder.ASC);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Sort.SORT_BY, "created_at");
		map.put(Sort.SORT_ORDER, "asc");
		assertEquals(map, sort.getQueryParameters());
		sort = new Sort(SearchSortableField.PRIORITY, SortOrder.DESC);
		map.put(Sort.SORT_BY, "priority");
		map.put(Sort.SORT_ORDER, "desc");
		assertEquals(map, sort.getQueryParameters());
	}

}
