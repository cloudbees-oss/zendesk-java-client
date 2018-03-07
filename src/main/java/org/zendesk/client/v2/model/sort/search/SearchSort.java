package org.zendesk.client.v2.model.sort.search;

import org.zendesk.client.v2.model.sort.Sort;
import org.zendesk.client.v2.model.sort.SortOrder;

/**
 * The Class SearchSort represents the sort properties to be associated with the
 * search request.
 */
public class SearchSort extends Sort {

	/**
	 * Instantiates a new SearchSort.
	 *
	 * @param field
	 *            the field
	 * @param order
	 *            the order
	 */
	public SearchSort(SearchSortableField field, SortOrder order) {
		super(field, order);
	}
}
