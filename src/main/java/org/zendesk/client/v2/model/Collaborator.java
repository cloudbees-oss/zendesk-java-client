package org.zendesk.client.v2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Collaborator implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String email;

    public Collaborator() {
    }

    protected Collaborator(String name) {
        this.name = name;
    }

    public Collaborator(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
