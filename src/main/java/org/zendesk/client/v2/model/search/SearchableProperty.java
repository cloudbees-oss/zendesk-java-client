package org.zendesk.client.v2.model.search;

/**
 * The Interface SearchableProperty.
 * 
 * @author tkurzawa 
 * @since 8 March 2018
 */
public interface SearchableProperty {

	/**
	 * Returns the keyword to be used during building of the search query
	 *
	 * @return the keyword representing the entity field
	 */
	public String getKeyword();
}
