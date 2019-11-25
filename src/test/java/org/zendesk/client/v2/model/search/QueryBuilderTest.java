package org.zendesk.client.v2.model.search;

import static org.junit.Assert.*;

import org.junit.Test;

public class QueryBuilderTest {

	SearchableProperty property = new SearchableProperty() {
		@Override
		public String getKeyword() {
			return "field";
		}
	};

	@Test
	public void testBuild() {
		QueryBuilder builder = new QueryBuilder();
		assertEquals("", builder.build());		
		builder = new QueryBuilder().addFieldSearch(new FieldSearch(property, SearchOp.EQUALS, "a"));
		assertEquals("field:a", builder.build());
		builder = new QueryBuilder().addFieldSearch(new FieldSearch(property, SearchOp.LESS_THAN, 5)).addFieldSearch(new FieldSearch(property, SearchOp.GREATER_THAN, 10));
		assertEquals("field<5 field>10", builder.build());
		builder = builder.addPhraseSearch("abcd");
		assertEquals("field<5 field>10 \"abcd\"", builder.build());
		builder = builder.addFieldSearch(new FieldSearch(property, SearchOp.EQUALS, "xyz"));
		assertEquals("field<5 field>10 field:xyz \"abcd\"", builder.build());
		builder = builder.addPhraseSearch("qwert");
		assertEquals("field<5 field>10 field:xyz \"abcd\" \"qwert\"", builder.build());
		builder = builder.setResultType(SearchResultType.TICKET);
		assertEquals("field<5 field>10 field:xyz \"abcd\" \"qwert\" type:ticket", builder.build());
	}

}
