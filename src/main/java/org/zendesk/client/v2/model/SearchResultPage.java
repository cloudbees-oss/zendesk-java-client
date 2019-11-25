package org.zendesk.client.v2.model;

import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class SearchResultPage represents the entities returned as a result of
 * the pageable request (list, search).
 *
 * @param <T>
 *            the SearchResultEntity type
 *            
 * @author tkurzawa 
 * @since 8 March 2018
 */
public class SearchResultPage<T extends SearchResultEntity> {

	/** The results. */
	private LinkedList<T> results;

	/** The next page. */
	@JsonProperty("next_page")
	private String nextPage;

	/** The prev page. */
	@JsonProperty("prev_page")
	private String prevPage;

	/** The count. */
	private int count;

	/**
	 * Instantiates a new SearchResultPage containing current page results/entities,
	 * count of all elements as well as links to the previous and next page
	 *
	 * @param results
	 *            the results
	 * @param nextPage
	 *            the next page url
	 * @param prevPage
	 *            the prev page url
	 * @param count
	 *            the count of all the elements to be returned
	 */
	public SearchResultPage(LinkedList<T> results, String nextPage, String prevPage, int count) {
		super();
		this.results = results;
		this.nextPage = nextPage;
		this.prevPage = prevPage;
		this.count = count;
	}

	/**
	 * Instantiates a new SearchResultPage when there are no results to be returned
	 */
	public SearchResultPage() {
		super();
		this.results = new LinkedList<T>();
		this.nextPage = "";
		this.prevPage = "";
		this.count = 0;
	}

	/**
	 * Gets the results.
	 *
	 * @return the results
	 */
	public LinkedList<T> getResults() {
		return results;
	}

	/**
	 * Sets the results.
	 *
	 * @param results
	 *            the new results
	 */
	public void setResults(LinkedList<T> results) {
		this.results = results;
	}

	/**
	 * Gets the next page.
	 *
	 * @return the next page
	 */
	public String getNextPage() {
		return nextPage;
	}

	/**
	 * Sets the next page.
	 *
	 * @param nextPage
	 *            the new next page
	 */
	public void setNextPage(String nextPage) {
		this.nextPage = nextPage;
	}

	/**
	 * Gets the prev page.
	 *
	 * @return the prev page
	 */
	public String getPrevPage() {
		return prevPage;
	}

	/**
	 * Sets the prev page.
	 *
	 * @param prevPage
	 *            the new prev page
	 */
	public void setPrevPage(String prevPage) {
		this.prevPage = prevPage;
	}

	/**
	 * Gets the count.
	 *
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Sets the count.
	 *
	 * @param count
	 *            the new count
	 */
	public void setCount(int count) {
		this.count = count;
	}

}
