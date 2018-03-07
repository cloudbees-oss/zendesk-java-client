package org.zendesk.client.v2.model.sort;

/**
 * Enums that contain fields that were enabled for sorting for a given type
 * should implement this interface.
 */
public interface SortableField {

	/**
	 * Returns the keyword to be used as the 'order_by' value, when sorting results
	 * based on the field
	 *
	 * @return the keyword
	 */
	public String getKeyword();
}
