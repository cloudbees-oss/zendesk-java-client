package org.zendesk.client.v2.model.request.page;

import java.util.HashMap;
import java.util.Map;

import org.zendesk.client.v2.model.sort.Sort;

/**
 * The Class PageableRequest.
 */
public abstract class PageableRequest implements IPageableRequest {

	/** The Constant PAGE. */
	public static final String PAGE = "page";
	
	/** The Constant PER_PAGE. */
	public static final String PER_PAGE = "per_page";
	
	/** The page. */
	private int page;

	/** The per page. */
	private int perPage;

	/** The sort. */
	private Sort sort;

	/**
	 * Instantiates a new PageableRequest with sort properites. Page and perPage
	 * properties to be set to the defaults on the requested endpoint.
	 *
	 * @param sort
	 *            the sort
	 */
	public PageableRequest(Sort sort) {
		this(0, 0, sort);
	}

	/**
	 * Instantiates a new PageableRequest.
	 *
	 * @param page
	 *            the page
	 * @param perPage
	 *            the per page
	 * @param sort
	 *            the sort
	 */
	public PageableRequest(int page, int perPage, Sort sort) {
		super();
		if(page < 0) {
			throw new IllegalArgumentException("Page cannot be less than 0");
		}
		if(perPage < 0) {
			throw new IllegalArgumentException("Per Page cannot be less than 1");
		}
		this.page = page;
		this.perPage = perPage;
		this.sort = sort;
	}

	/**
	 * Instantiates a new PageableRequest without sorting properties.
	 *
	 * @param page            the page
	 * @param perPage the per page
	 */
	public PageableRequest(int page, int perPage) {
		this(page, perPage, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zendesk.client.v2.model.IPageableRequest#getPage()
	 */
	public int getPage() {
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zendesk.client.v2.model.IPageableRequest#setPage(int)
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zendesk.client.v2.model.IPageableRequest#getPerPage()
	 */
	public int getPerPage() {
		return perPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zendesk.client.v2.model.IPageableRequest#setPerPage(int)
	 */
	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zendesk.client.v2.model.IPageableRequest#getSort()
	 */
	public Sort getSort() {
		return sort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zendesk.client.v2.model.IPageableRequest#setSort(org.zendesk.client.v2.
	 * model.sort.Sort)
	 */
	public void setSort(Sort sort) {
		this.sort = sort;
	}

	
	/* (non-Javadoc)
	 * @see org.zendesk.client.v2.model.request.page.IPageableRequest#getQueryParameters()
	 */
	@Override
	public Map<String, Object> getQueryParameters() {
		Map<String, Object> params = new HashMap<String, Object>();
		if (page > 1) {
			params.put(PAGE, page);
		}
		if(perPage > 0) {
			params.put(PER_PAGE, perPage);
		}
		if(sort != null) {
			params.putAll(sort.getQueryParameters());
		}
		return params;
	}
}
