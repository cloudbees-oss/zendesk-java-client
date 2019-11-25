package org.zendesk.client.v2.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.LinkedList;

import org.junit.Test;

public class SearchResultPageTest {

	@Test
	public void testSearchResultPageLinkedListOfTStringStringInt() {
		LinkedList<SearchResultEntity> results = new LinkedList<SearchResultEntity>();
		String nextPage = "nextPage";
		String prevPage = "prevPage";
		int count = 123;
		SearchResultPage<SearchResultEntity> page = new SearchResultPage<>(results, nextPage, prevPage, count);
		assertNotNull(page);
		assertEquals(0, page.getResults().size());
		assertEquals(nextPage, page.getNextPage());
		assertEquals(prevPage, page.getPrevPage());
		assertEquals(count, page.getCount());
	}

	@Test
	public void testSearchResultPage() {
		SearchResultPage<SearchResultEntity> page = new SearchResultPage<>();
		assertNotNull(page);
		assertEquals(0, page.getResults().size());
		assertEquals("", page.getNextPage());
		assertEquals("", page.getPrevPage());
		assertEquals(0, page.getCount());
	}

}
