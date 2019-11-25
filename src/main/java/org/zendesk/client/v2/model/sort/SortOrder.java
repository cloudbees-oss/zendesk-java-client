package org.zendesk.client.v2.model.sort;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The Enumeration for sort order.
 * 
 * @author tkurzawa 
 * @since 8 March 2018
 */
public enum SortOrder {

	/** The asc. */
	ASC("asc"),
	/** The desc. */
	DESC("desc");

	/** The order. */
	private String order;

	/**
	 * Instantiates a new SortOrder.
	 *
	 * @param order
	 *            the order
	 */
	private SortOrder(String order) {
		this.order = order;
	}

	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	@JsonValue
	public String getOrder() {
		return order;
	}

	/**
	 * Gets the SortOrder by order.
	 *
	 * @param order
	 *            the order
	 * @return the by order
	 */
	@JsonCreator
	public static SortOrder getByOrder(String order) {
		SortOrder sortOrder = null;
		for (SortOrder sortOrderEnum : SortOrder.values()) {
			if (sortOrderEnum.getOrder().equalsIgnoreCase(order)) {
				sortOrder = sortOrderEnum;
			}
		}
		return sortOrder;
	}
}
