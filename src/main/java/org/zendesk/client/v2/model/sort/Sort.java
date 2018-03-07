package org.zendesk.client.v2.model.sort;

import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * 
 * The Class Sort represents the field based on which returned list of entities
 * should be sorted by and order in which entities should be sorted.
 * 
 * @author Tomasz Kurzawa (tzkurzawa@gmail.com)
 *
 */
public class Sort {

	/** The sort by. */
	public static final String SORT_BY = "sort_by";

	/** The sort order. */
	public static final String SORT_ORDER = "sort_order";

	/** The field. */
	private SortableField field;

	/** The order. */
	private SortOrder order;

	/**
	 * Instantiates a new sort.
	 *
	 * @param field
	 *            the field
	 * @param order
	 *            the order
	 */
	public Sort(SortableField field, SortOrder order) {
		if (field == null) {
			throw new IllegalArgumentException("Field cannot be null.");
		}
		if (order == null) {
			throw new IllegalArgumentException("Order can not be null.");
		}
		this.field = field;
		this.order = order;
	}

	/**
	 * Gets the field.
	 *
	 * @return the field
	 */
	public SortableField getField() {
		return field;
	}

	/**
	 * Sets the field.
	 *
	 * @param field
	 *            the new field
	 */
	public void setField(SortableField field) {
		this.field = field;
	}

	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	public SortOrder getOrder() {
		return order;
	}

	/**
	 * Sets the order.
	 *
	 * @param order
	 *            the new order
	 */
	public void setOrder(SortOrder order) {
		this.order = order;
	}

	/**
	 * Returns map with name of the sort query parameters and values
	 *
	 * @return the query parameters
	 */
	public Map<String, Object> getQueryParameters() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(SORT_BY, field.getKeyword());
		parameters.put(SORT_ORDER, order.getOrder());
		return parameters;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Sort [field=" + field + ", order=" + order + "]";
	}
}
