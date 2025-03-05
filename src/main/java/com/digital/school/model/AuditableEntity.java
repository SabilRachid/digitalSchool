package com.digital.school.model;


import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;


@MappedSuperclass
public abstract class AuditableEntity  extends BaseEntity{

    private static final long serialVersionUID = -5225634530609871236L;

    @Embedded
    private Auditable auditable = new Auditable();

    @PrePersist
    public void prePersist() {
        if (this.auditable == null) {
            this.auditable = new Auditable();
        }
        this.auditable.onCreate();
    }

    @PreUpdate
    public void preUpdate() {
        if (this.auditable == null) {
            this.auditable = new Auditable();
        }
        this.auditable.onUpdate();
    }

    public Auditable getAuditable() {
        return auditable;
    }

    public void setAuditable(Auditable auditable) {
        this.auditable = auditable;
    }


}
