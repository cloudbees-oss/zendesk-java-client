package org.zendesk.client.v2.model.request.page;

import org.zendesk.client.v2.model.sort.ticket.TicketSort;

/**
 * The Class specifies the PageableRequest for Search REST endpoint.
 * 
 * @author tkurzawa 
 * @since 8 March 2018
 */
public class TicketPageableRequest extends PageableRequest {

	/**
	 * Instantiates a new TicketPageableRequest
	 *
	 * @param sort
	 *            the sort
	 */
	public TicketPageableRequest(TicketSort sort) {
		super(sort);
	}

	/**
	 * Instantiates a new TicketPageableRequest
	 *
	 * @param page
	 *            the page
	 * @param perPage
	 *            the per page
	 * @param sort
	 *            the sort
	 */
	public TicketPageableRequest(int page, int perPage, TicketSort sort) {
		super(page, perPage, sort);
	}

}
