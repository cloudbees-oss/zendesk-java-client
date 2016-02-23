package org.zendesk.client.v2.model.schedules;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Holiday implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private Long id;
    private String startDate;
    private String endDate;

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

    @JsonProperty("start_date")
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate( String startDate ) {
        this.startDate = startDate;
    }

    @JsonProperty("end_date")
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate( String endDate ) {
        this.endDate = endDate;
    }
}
