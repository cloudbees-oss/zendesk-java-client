package org.zendesk.client.v2.model.request.page;

import java.util.Map;

import org.zendesk.client.v2.model.sort.Sort;

// TODO: Auto-generated Javadoc
/**
 * The Interface IPageableRequest represents the request to return entities that
 * should be displayed on the specific page with per page number of elements.
 * Sort properties can be set as well.
 */
public interface IPageableRequest {

	/**
	 * Gets the page to be returned.
	 *
	 * @return the page
	 */
	public int getPage();

	/**
	 * Sets the page to be returned.
	 *
	 * @param page
	 *            the new page
	 */
	public void setPage(int page);

	/**
	 * Gets the number of elements per page.
	 *
	 * @return the per page
	 */
	public int getPerPage();

	/**
	 * Sets the number of elements per page.
	 *
	 * @param perPage
	 *            the new per page
	 */
	public void setPerPage(int perPage);

	/**
	 * Gets the sort properties.
	 *
	 * @return the sort
	 */
	public Sort getSort();

	/**
	 * Sets the sort properties.
	 *
	 * @param sort
	 *            the new sort
	 */
	public void setSort(Sort sort);

	/**
	 * Returns the map of query parameters and values
	 *
	 * @return the query parameters
	 */
	public Map<String, Object> getQueryParameters();
}
