package org.zendesk.client.v2.model.search;

import static org.junit.Assert.*;

import org.junit.Test;

public class SearchOpTest {

	@Test
	public void testGetOp() {
		assertEquals(":", SearchOp.EQUALS.getOp());
		assertEquals(">", SearchOp.GREATER_THAN.getOp());
		assertEquals(">=", SearchOp.GREATER_THAN_OR_EQUAL.getOp());
		assertEquals("<", SearchOp.LESS_THAN.getOp());
		assertEquals("<=", SearchOp.LESS_THAN_OR_EQUAL.getOp());
	}

	@Test
	public void testGetByOp() {
		assertEquals(SearchOp.EQUALS, SearchOp.getByOp(":"));
		assertEquals(SearchOp.EQUALS, SearchOp.getByOp(" : "));
		assertEquals(SearchOp.GREATER_THAN, SearchOp.getByOp(">"));
		assertEquals(SearchOp.GREATER_THAN, SearchOp.getByOp(">   "));
		assertEquals(SearchOp.GREATER_THAN_OR_EQUAL, SearchOp.getByOp(">="));
		assertEquals(SearchOp.GREATER_THAN_OR_EQUAL, SearchOp.getByOp("  >="));
		assertEquals(SearchOp.LESS_THAN, SearchOp.getByOp("<"));
		assertEquals(SearchOp.LESS_THAN, SearchOp.getByOp("< "));
		assertEquals(SearchOp.LESS_THAN_OR_EQUAL, SearchOp.getByOp("<="));
		assertEquals(SearchOp.LESS_THAN_OR_EQUAL, SearchOp.getByOp("<=   "));

		assertNull(SearchOp.getByOp("<>"));
		assertNull(SearchOp.getByOp(null));
	}

}
