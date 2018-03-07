package org.zendesk.client.v2.model.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration contains allowed operations to be used during building of the
 * query against entity fields.
 */
public enum SearchOp {

	/** The equals. */
	EQUALS(":"),

	/** The less than. */
	LESS_THAN("<"),

	/** The greater than. */
	GREATER_THAN(">"),

	/** The less than or equal. */
	LESS_THAN_OR_EQUAL("<="),

	/** The greater than or equal. */
	GREATER_THAN_OR_EQUAL(">=");

	/** The op. */
	private String op;

	/**
	 * Instantiates a new search op.
	 *
	 * @param op
	 *            the op
	 */
	private SearchOp(String op) {
		this.op = op;
	}

	/**
	 * Gets the op.
	 *
	 * @return the op
	 */
	@JsonValue
	public String getOp() {
		return op;
	}

	/**
	 * Gets the by op.
	 *
	 * @param op
	 *            the op
	 * @return the by op
	 */
	@JsonCreator
	public static SearchOp getByOp(String op) {
		SearchOp searchOp = null;
		if(op != null) {
			for (SearchOp searchOpEnum : SearchOp.values()) {
				if (searchOpEnum.getOp().equalsIgnoreCase(op.trim())) {
					searchOp = searchOpEnum;
				}
			}
		}
		return searchOp;
	}

}
