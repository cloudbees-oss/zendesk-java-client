package org.zendesk.client.v2.model.sort.ticket;

import org.zendesk.client.v2.model.sort.Sort;
import org.zendesk.client.v2.model.sort.SortOrder;

/**
 * The Class TicketSort represents the sort properties to be associated with the
 * list ticket request.
 */
public class TicketSort extends Sort {

	/**
	 * Instantiates a new TcketSort.
	 *
	 * @param field
	 *            the field
	 * @param order
	 *            the order
	 */
	public TicketSort(TicketSortableField field, SortOrder order) {
		super(field, order);
	}

}
