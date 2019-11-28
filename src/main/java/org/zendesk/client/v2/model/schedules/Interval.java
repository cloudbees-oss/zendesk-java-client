package org.zendesk.client.v2.model.schedules;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Interval implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long startTime;
    private Long endTime;

    /*
     * Integer representation of the interval start time
     */
    @JsonProperty("start_time")
    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime( Long startTime ) {
        this.startTime = startTime;
    }

    /*
     * Integer representation of the interval end time
     */
    @JsonProperty("end_time")
    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime( Long endTime ) {
        this.endTime = endTime;
    }
}
