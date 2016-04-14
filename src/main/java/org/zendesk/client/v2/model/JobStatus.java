package org.zendesk.client.v2.model;

import java.io.Serializable;
import java.util.List;

public class JobStatus<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String url;
    private Integer total;
    private Integer progress;
    private JobStatusEnum status;
    private String message;
    private List<T> results;
    private Class<T> resultsClass;

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

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public Class<T> getResultsClass() {
        return resultsClass;
    }

    public void setResultsClass(Class<T> resultsClass) {
        this.resultsClass = resultsClass;
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
