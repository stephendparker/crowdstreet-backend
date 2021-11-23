package com.crowdstreet.parker.backend.data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DocPayload {

    public static final String INTIAL_STATUS = "NOT STARTED";

    @Id
    public String id;
    public String body;
    public String status;
    public String detail;


    // Default constructor for JPA
    public DocPayload() {
    }

    public DocPayload(String id, String body) {
        this.id = id;
        this.status = DocPayload.INTIAL_STATUS;
        this.body = body;
    }
}
