package org.zendesk.client.v2.model.search;

import org.zendesk.client.v2.model.Group;
import org.zendesk.client.v2.model.Organization;
import org.zendesk.client.v2.model.SearchResultEntity;
import org.zendesk.client.v2.model.Ticket;
import org.zendesk.client.v2.model.Topic;
import org.zendesk.client.v2.model.User;
import org.zendesk.client.v2.model.hc.Article;

/**
 * Enumeration of the types supported to be returned as a result of the search
 * query.
 */
public enum SearchResultType {

	/** The ticket. */
	TICKET("ticket", Ticket.class),

	/** The user. */
	USER("user", User.class),

	/** The group. */
	GROUP("group", Group.class),

	/** The organization. */
	ORGANIZATION("organization", Organization.class),

	/** The topic. */
	TOPIC("topic", Topic.class),

	/** The article. */
	ARTICLE("article", Article.class);

	/** The name. */
	private String name;

	/** The clazz. */
	private Class<? extends SearchResultEntity> clazz;

	/**
	 * Instantiates a new search result type.
	 *
	 * @param name
	 *            the name
	 * @param clazz
	 *            the clazz
	 */
	private SearchResultType(String name, Class<? extends SearchResultEntity> clazz) {
		this.name = name;
		this.clazz = clazz;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the type class.
	 *
	 * @return the type class
	 */
	public Class<? extends SearchResultEntity> getTypeClass() {
		return clazz;
	}
}
