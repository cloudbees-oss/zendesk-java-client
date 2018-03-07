package org.zendesk.client.v2.model.search;

import static org.junit.Assert.*;

import org.junit.Test;

public class FieldSearchTest {

	SearchableProperty p = new SearchableProperty() {
		@Override
		public String getKeyword() {
			return "BOO";
		}
	};
	SearchableProperty emptyProperty = new SearchableProperty() {
		@Override
		public String getKeyword() {
			return "";
		}
	};
	
	@Test
	public void testFieldSearchSearchablePropertySearchOpString() {
		FieldSearch fieldSearch = new FieldSearch(p, SearchOp.EQUALS, "abc");
		assertNotNull(fieldSearch);
		assertFalse(fieldSearch.isExclude());
		assertEquals(p, fieldSearch.getProperty());
		assertEquals(SearchOp.EQUALS, fieldSearch.getOp());
		assertEquals("abc", fieldSearch.getValue());
	}

	@Test
	public void testFieldSearchBooleanSearchablePropertySearchOpString() {
		FieldSearch fieldSearch = new FieldSearch(true, p, SearchOp.EQUALS, "abc");
		assertNotNull(fieldSearch);
		assertTrue(fieldSearch.isExclude());
		assertEquals(p, fieldSearch.getProperty());
		assertEquals(SearchOp.EQUALS, fieldSearch.getOp());
		assertEquals("abc", fieldSearch.getValue());
	}
	

	@Test(expected=IllegalArgumentException.class)
	public void testFieldSearch_whenSearchOpNull_thenIllegalArgException() {
		new FieldSearch(p, null, "value");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFieldSearch_whenPropertyNull_thenIllegalArgException() {
		new FieldSearch(null, SearchOp.EQUALS, "value");
	}

	@Test
	public void testToString() {
		FieldSearch fieldSearch = new FieldSearch(p, SearchOp.EQUALS, "abc");
		assertEquals("BOO:abc", fieldSearch.toString());
		fieldSearch = new FieldSearch(p, SearchOp.LESS_THAN, 5);
		assertEquals("BOO<5", fieldSearch.toString());
		fieldSearch = new FieldSearch(true, p, SearchOp.GREATER_THAN_OR_EQUAL, 5);
		assertEquals("-BOO>=5", fieldSearch.toString());
		fieldSearch = new FieldSearch(emptyProperty, SearchOp.EQUALS, "abc");
		assertEquals("abc", fieldSearch.toString());
	}

}
