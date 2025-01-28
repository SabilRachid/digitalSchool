package com.digital.school.model;


import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.domain.Auditable;

@MappedSuperclass
public abstract class AuditableEntity  extends BaseEntity{

    private static final long	serialVersionUID	= -5225634530609871236L;

    @Embedded
    private Auditable auditable;

    public Auditable getAuditable() {
        return auditable;
    }

    public void setAuditable(Auditable auditable) {
        this.auditable = auditable;
    }


}
