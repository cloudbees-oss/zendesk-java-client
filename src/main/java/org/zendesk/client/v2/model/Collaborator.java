package org.zendesk.client.v2.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Collaborator implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String email;

    public Collaborator() {
    }

    protected Collaborator(String name) {
        this.name = name;
        this.email = email;
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
