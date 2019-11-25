package org.zendesk.client.v2.model.search;

/**
 * The Class FieldSearch represents the property that should have specific value
 * in the searched entity. It also enables the ability to specify id entity
 * should not have specified property.
 * 
 * @author tkurzawa 
 * @since 8 March 2018
 */
public class FieldSearch {

	/** The exclude. */
	private boolean exclude = false;

	/** The property. */
	private SearchableProperty property;

	/** The op. */
	private SearchOp op;

	/** The value. */
	private Object value;

	/**
	 * Instantiates a FieldSearch for a specific property, with specific operation
	 * and value.
	 *
	 * @param property
	 *            the property
	 * @param op
	 *            the op
	 * @param value
	 *            the value
	 */
	public FieldSearch(SearchableProperty property, SearchOp op, Object value) {
		this(false, property, op, value);
	}

	/**
	 * Instantiates a FieldSearch for a specific property, with specific operation
	 * and value. Setting the exclude parameter to false will look for entities
	 * where property does not contain property with matching value.
	 *
	 * @param exclude
	 *            the exclude
	 * @param property
	 *            the property
	 * @param op
	 *            the op
	 * @param value
	 *            the value
	 */
	public FieldSearch(boolean exclude, SearchableProperty property, SearchOp op, Object value) {
		if(property == null) {
			throw new IllegalArgumentException("Property can not be null.");
		}
		if(op == null) {
			throw new IllegalArgumentException("Search operation can not be null.");
		}
		this.exclude = exclude;
		this.property = property;
		this.op = op;
		this.value = value;
	}

	/**
	 * Checks if is exclude.
	 *
	 * @return true, if is exclude
	 */
	public boolean isExclude() {
		return exclude;
	}

	/**
	 * Sets the exclude.
	 *
	 * @param exclude
	 *            the new exclude
	 */
	public void setExclude(boolean exclude) {
		this.exclude = exclude;
	}

	/**
	 * Gets the property.
	 *
	 * @return the property
	 */
	public SearchableProperty getProperty() {
		return property;
	}

	/**
	 * Sets the property.
	 *
	 * @param property
	 *            the new property
	 */
	public void setProperty(SearchableProperty property) {
		this.property = property;
	}

	/**
	 * Gets the op.
	 *
	 * @return the op
	 */
	public SearchOp getOp() {
		return op;
	}

	/**
	 * Sets the op.
	 *
	 * @param op
	 *            the new op
	 */
	public void setOp(SearchOp op) {
		this.op = op;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value
	 *            the new value
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if(exclude) {
			sb.append("-");
		}
		if("".equals(property.getKeyword())){
			// In case of ID there is no keyword
			return sb.append(value).toString();
		}
		return sb.append(property.getKeyword()).append(op.getOp()).append(value.toString()).toString();
	}

}
