package com.digital.school.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;






@Embeddable
public class Auditable implements Serializable
{
    private static final long	serialVersionUID	= 5322674309593123145L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CREATED", nullable=true, updatable=false)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATED")
    private Date updated;


    @PrePersist
    public void prePersist() {
        if (this.created == null) {
            this.created = new Date();
        }
    }


    public Auditable(Date created)
    {
        this.created = created;
    }
    public Auditable()
    {
    }
    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public Date getUpdated()
    {
        return updated;
    }

    public void setUpdated(Date updated)
    {
        this.updated = updated;
    }


    public Date getLastModified()
    {
        return (updated!=null)?updated:created;
    }


}
