package org.zendesk.client.v2.model.schedules;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String timeZone;
    private List<Interval> intervals;
    private Date createdAt;
    private Date updatedAt;

    /**
     * Automatically assigned upon creation
     * @return Schedule ID
     */
    public Long getId() {
      return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    /**
     * @return Name of the Schedule
     */
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    /**
     * @return Time zone of the schedule
     */
    @JsonProperty("time_zone")
    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone( String timeZone ) {
        this.timeZone = timeZone;
    }

    /**
     * @return List of intervals for the schedule
     */
    public List<Interval> getIntervals() {
        return intervals;
    }

    public void setIntervals( List<Interval> intervals ) {
        this.intervals = intervals;
    }

    /**
     * @return Time the schedule was created
     */
    @JsonProperty("created_at")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt( Date createdAt ) {
        this.createdAt = createdAt;
    }

    /**
     * @return Time the schedule was last updated
     */
    @JsonProperty("updated_at")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt( Date updatedAt ) {
        this.updatedAt = updatedAt;
    }
}
