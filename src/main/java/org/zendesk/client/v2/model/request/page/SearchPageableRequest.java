package org.zendesk.client.v2.model.request.page;

import org.zendesk.client.v2.model.sort.search.SearchSort;

/**
 * The Class specifies the PageableRequest for Search REST endpoint.
 * 
 * @author tkurzawa 
 * @since 8 March 2018
 */
public class SearchPageableRequest extends PageableRequest {

	/**
	 * Instantiates a new SearchPageableRequest with sort properties
	 *
	 * @param sort
	 *            the sort
	 */
	public SearchPageableRequest(SearchSort sort) {
		super(sort);
	}

	/**
	 * Instantiates a new SearchPageableRequest
	 *
	 * @param page
	 *            the page
	 * @param perPage
	 *            the per page
	 * @param sort
	 *            the sort
	 */
	public SearchPageableRequest(int page, int perPage, SearchSort sort) {
		super(page, perPage, sort);
	}

}
