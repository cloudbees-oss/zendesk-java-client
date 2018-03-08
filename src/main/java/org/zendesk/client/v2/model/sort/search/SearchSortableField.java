package org.zendesk.client.v2.model.sort.search;

import org.zendesk.client.v2.model.sort.SortableField;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration for search request sortable fields.
 * 
 * @author tkurzawa 
 * @since 8 March 2018
 */
public enum SearchSortableField implements SortableField {

	/** The updated at. */
	UPDATED_AT("updated_at"),

	/** The created at. */
	CREATED_AT("created_at"),

	/** The priority. */
	PRIORITY("priority"),

	/** The status. */
	STATUS("status"),

	/** The ticket type. */
	TICKET_TYPE("ticket_type");

	/** The keyword. */
	private String keyword;

	/**
	 * Instantiates a new search sortable field.
	 *
	 * @param keyword
	 *            the keyword
	 */
	private SearchSortableField(String keyword) {
		this.keyword = keyword;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zendesk.client.v2.model.sort.SortableField#getKeyword()
	 */
	@JsonValue
	public String getKeyword() {
		return keyword;
	}

	/**
	 * Gets the SearchSortableField by keyword.
	 *
	 * @param keyword
	 *            the keyword
	 * @return the by keyword
	 */
	@JsonCreator
	public static SearchSortableField getByKeyword(String keyword) {
		SearchSortableField searchField = null;
		for (SearchSortableField searchFieldEnum : SearchSortableField.values()) {
			if (searchFieldEnum.getKeyword().equalsIgnoreCase(keyword)) {
				searchField = searchFieldEnum;
			}
		}
		return searchField;
	}

}
