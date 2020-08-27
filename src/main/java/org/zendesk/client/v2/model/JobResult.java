package org.zendesk.client.v2.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * https://developer.zendesk.com/rest_api/docs/support/job_statuses#results
 * <p>
 * Result entries have various properties. There is no clear description thus let's use a generic Map
 * Only the "id" of the object in Zendesk (Always a Long) seems to be always present.
 * We define few others helpers.
 */
public class JobResult extends HashMap<String, Object> implements Serializable {

    /**
     * The account ID
     */
    public static final String ACCOUNT_ID = "account_id";
    /**
     * the action the job attempted
     */
    public static final String ACTION = "action";
    /**
     * The details about the error
     */
    public static final String DETAILS = "details";
    /**
     * The user email
     */
    public static final String EMAIL = "email";
    /**
     * An error code
     */
    public static final String ERROR = "error";
    /**
     * The user external ID
     */
    public static final String EXTERNAL_ID = "external_id";
    /**
     * the id of the resource created or updated
     */
    public static final String ID = "id";
    /**
     * the index number of the result
     */
    public static final String INDEX = "index";
    /**
     * the status of the action
     */
    public static final String STATUS = "status";
    /**
     * whether the action was successful or not
     */
    public static final String SUCCESS = "success";

    public Long getAccountId() {
        return (Long) get(ACCOUNT_ID);
    }

    public String getAction() {
        return (String) get(ACTION);
    }

    public String getDetails() {
        return (String) get(DETAILS);
    }

    public String getEmail() {
        return (String) get(EMAIL);
    }

    public String getError() {
        return (String) get(ERROR);
    }

    public String getExternalId() {
        return (String) get(EXTERNAL_ID);
    }

    public Long getId() {
        return (Long) get(ID);
    }

    public Long getIndex() {
        return (Long) get(INDEX);
    }

    public String getStatus() {
        return (String) get(STATUS);
    }

    public Boolean getSuccess() {
        return (Boolean) get(SUCCESS);
    }
}
