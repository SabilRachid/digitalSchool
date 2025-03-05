package com.digital.school.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;


@Embeddable
public class Auditable implements Serializable
{
    private static final long	serialVersionUID	= 5322674309593123145L;
    @Column(name = "created", updatable = false)
    private Date created;

    @Column(name = "updated")
    private Date updated;

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.created = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated = new Date();
    }

    // Getters and setters
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
