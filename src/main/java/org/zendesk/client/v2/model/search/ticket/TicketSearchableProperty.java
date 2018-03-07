package org.zendesk.client.v2.model.search.ticket;

import org.zendesk.client.v2.model.search.SearchableProperty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The Enum TicketSearchableProperty contains the Ticket fields with matching
 * keywords that are enabled for searching/quering
 */
public enum TicketSearchableProperty implements SearchableProperty {

	/** The id. */
	ID(""),

	/** The created. */
	CREATED("created"),

	/** The updated. */
	UPDATED("updated"),

	/** The solved. */
	SOLVED("solved"),

	/** The due date. */
	DUE_DATE("due_date"),

	/** The assignee. */
	ASSIGNEE("assignee"),

	/** The submitter. */
	SUBMITTER("submitter"),

	/** The requester. */
	REQUESTER("requester"),

	/** The subject. */
	SUBJECT("subject"),

	/** The description. */
	DESCRIPTION("description"),

	/** The status. */
	STATUS("status"),

	/** The ticket type. */
	TICKET_TYPE("ticket_type"),

	/** The priority. */
	PRIORITY("priority"),

	/** The group. */
	GROUP("group"),

	/** The organization. */
	ORGANIZATION("organization"),

	/** The tags. */
	TAGS("tags"),

	/** The via. */
	VIA("via"),

	/** The commenter. */
	COMMENTER("commenter"),

	/** The cc. */
	CC("cc"),

	/** The custom field. */
	CUSTOM_FIELD("fieldvalue"),

	/** The brand. */
	BRAND("brand");

	/** The keyword. */
	private String keyword;

	/**
	 * Instantiates a new TicketSearchableProperty
	 *
	 * @param keyword
	 *            the keyword
	 */
	private TicketSearchableProperty(String keyword) {
		this.keyword = keyword;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zendesk.client.v2.model.search.SearchableProperty#getKeyword()
	 */
	@JsonValue
	public String getKeyword() {
		return keyword;
	}

	/**
	 * Returns the {@link TicketSearchableProperty} by keyword
	 *
	 * @param keyword
	 *            the keyword
	 * @return the {@link TicketSearchableProperty}
	 */
	@JsonCreator
	public static TicketSearchableProperty getByKeyword(String keyword) {
		TicketSearchableProperty searchableProp = null;
		if (keyword != null) {
			for (TicketSearchableProperty fieldEnum : TicketSearchableProperty.values()) {
				if (fieldEnum.getKeyword().equalsIgnoreCase(keyword.trim())) {
					searchableProp = fieldEnum;
				}
			}
		}
		return searchableProp;
	}
}
