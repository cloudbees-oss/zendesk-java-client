package org.zendesk.client.v2.model.sort.ticket;

import org.zendesk.client.v2.model.sort.SortableField;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration for search request sortable fields.
 * 
 * @author tkurzawa 
 * @since 8 March 2018
 */
public enum TicketSortableField implements SortableField {

	/** The assignee. */
	ASSIGNEE("assignee"),

	/** The assignee name. */
	ASSIGNEE_NAME("assignee.name"),

	/** The created at. */
	CREATED_AT("created_at"),

	/** The group. */
	GROUP("group"),

	/** The id. */
	ID("id"),

	/** The locale. */
	LOCALE("locale"),

	/** The requester. */
	REQUESTER("requester"),

	/** The requester name. */
	REQUESTER_NAME("requester.name"),

	/** The status. */
	STATUS("status"),

	/** The subject. */
	SUBJECT("subject"),

	/** The updated at. */
	UPDATED_AT("updated_at");

	/** The keyword. */
	private String keyword;

	/**
	 * Instantiates a new TicketSortableField.
	 *
	 * @param keyword
	 *            the keyword
	 */
	private TicketSortableField(String keyword) {
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
	 * Gets the TicketSortableField by keyword.
	 *
	 * @param keyword
	 *            the keyword
	 * @return the by keyword
	 */
	@JsonCreator
	public static TicketSortableField getByKeyword(String keyword) {
		TicketSortableField ticketField = null;
		for (TicketSortableField ticketFieldEnum : TicketSortableField.values()) {
			if (ticketFieldEnum.getKeyword().equalsIgnoreCase(keyword)) {
				ticketField = ticketFieldEnum;
			}
		}
		return ticketField;
	}

}
