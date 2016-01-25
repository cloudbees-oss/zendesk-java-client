package org.zendesk.client.v2.model.schedules;

import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Holiday implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private Long id;
    private Long startDate;
    private Long endDate;

    /*
     * Name of the holiday
     */
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    /*
     * Automatically assigned upon creation
     */
    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    /*
     *  Integer representation of the holiday start date
     */
    @JsonProperty("start_date")
    public Long getStartDay() {
        return startDate;
    }

    public void setStartDay( Long startDate ) {
        this.startDate = startDate;
    }

    /*
     *  Integer representation of the holiday end date
     */
    @JsonProperty("end_date")
    public Long getEndDay() {
        return endDate;
    }

    public void setEndDay( Long endDate ) {
        this.endDate = endDate;
    }
}
