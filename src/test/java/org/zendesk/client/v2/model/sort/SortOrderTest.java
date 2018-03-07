package org.zendesk.client.v2.model.sort;

import static org.junit.Assert.*;

import org.junit.Test;

public class SortOrderTest {

	@Test
	public void testGetOrder() {
		assertEquals("asc", SortOrder.ASC.getOrder());
		assertEquals("desc", SortOrder.DESC.getOrder());
	}

	@Test
	public void testGetByOrder() {
		assertEquals(SortOrder.ASC, SortOrder.getByOrder("asc"));
		assertEquals(SortOrder.ASC, SortOrder.getByOrder("aSc"));
		assertEquals(SortOrder.ASC, SortOrder.getByOrder("ASC"));

		assertEquals(SortOrder.DESC, SortOrder.getByOrder("desc"));
		assertEquals(SortOrder.DESC, SortOrder.getByOrder("dESc"));
		assertEquals(SortOrder.DESC, SortOrder.getByOrder("DESC"));
		
		assertNull(SortOrder.getByOrder("order"));
		assertNull(SortOrder.getByOrder(null));
	}

}
