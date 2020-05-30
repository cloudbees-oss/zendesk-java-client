package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.List;

public class JobStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String url;
    private Integer total;
    private Integer progress;
    private JobStatusEnum status;
    private String message;
    private List<JobResult> results;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public JobStatusEnum getStatus() {
        return status;
    }

    public void setStatus(JobStatusEnum status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonFormat(with = {JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
            JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED})
    public List<JobResult> getResults() {
        return results;
    }

    public void setResults(List<JobResult> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "JobStatus{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", total=" + total +
                ", progress=" + progress +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", results=" + results +
                '}';
    }

    public enum JobStatusEnum {
        working,
        failed,
        completed,
        killed,
        queued
    }
}
