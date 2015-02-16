package org.zendesk.client.v2.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketForm implements SearchResultEntity {

	private Long id;
	private String name;
	@JsonProperty("raw_name")
	private String rawName;
	@JsonProperty("display_name")
	private String displayName;
	@JsonProperty("raw_display_name")
	private String rawDisplayName;
	private int position;
	private boolean active;
	@JsonProperty("end_user_visible")
	private boolean endUserVisible;
	@JsonProperty("default")
	private boolean defaultForm;
	@JsonProperty("ticket_field_ids")
	private List<Integer> ticketFieldIds;
	@JsonProperty("created_at")
    private Date createdAt;
	@JsonProperty("updated_at")
    private Date updatedAt;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRawName() {
		return rawName;
	}
	public void setRawName(String rawName) {
		this.rawName = rawName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getRawDisplayName() {
		return rawDisplayName;
	}
	public void setRawDisplayName(String rawDisplayName) {
		this.rawDisplayName = rawDisplayName;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public boolean isEndUserVisible() {
		return endUserVisible;
	}
	public void setEndUserVisible(boolean endUserVisible) {
		this.endUserVisible = endUserVisible;
	}
	public boolean isDefaultForm() {
		return defaultForm;
	}
	public void setDefaultForm(boolean defaultForm) {
		this.defaultForm = defaultForm;
	}
	public List<Integer> getTicketFieldIds() {
		return ticketFieldIds;
	}
	public void setTicketFieldIds(List<Integer> ticketFieldIds) {
		this.ticketFieldIds = ticketFieldIds;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
