package org.zendesk.client.v2.model.search;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class QueryBuilder allows to easily create queries to search for entities
 * with matching properties.
 * 
 * @author tkurzawa 
 * @since 8 March 2018
 */
public class QueryBuilder {

	/** The field search. */
	private List<FieldSearch> fieldSearch;

	/** The phrase search. */
	private List<String> phraseSearch;
	
	/** The result type. */
	private SearchResultType resultType;

	/**
	 * Instantiates a new query builder.
	 */
	public QueryBuilder() {
		this.fieldSearch = new ArrayList<FieldSearch>();
		this.phraseSearch = new ArrayList<String>();
	}

	/**
	 * Adds the field search.
	 *
	 * @param fieldSearch
	 *            the field search
	 * @return the query builder
	 */
	public QueryBuilder addFieldSearch(FieldSearch fieldSearch) {
		this.fieldSearch.add(fieldSearch);
		return this;
	}

	/**
	 * Adds the phrase search.
	 *
	 * @param phrase
	 *            the phrase
	 * @return the query builder
	 */
	public QueryBuilder addPhraseSearch(String phrase) {
		this.phraseSearch.add(phrase);
		return this;
	}
	
	public QueryBuilder setResultType(SearchResultType resultType) {
		this.resultType = resultType;
		return this;
	}

	/**
	 * Builds the.
	 *
	 * @return the string
	 */
	public String build() {
		StringBuffer sb = new StringBuffer();
		for (FieldSearch field : fieldSearch) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(field.toString());
		}
		for (String phrase : phraseSearch) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append("\"").append(phrase).append("\"");
		}
		if(resultType != null) {
			if(sb.length() > 0) {
				sb.append(" ");
			}
			sb.append("type:").append(resultType.getName());
		}
		return sb.toString();
	}
}
