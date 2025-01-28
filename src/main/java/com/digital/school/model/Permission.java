package com.digital.school.model;

import jakarta.persistence.*;
import com.digital.school.model.enumerated.PermissionName;

@Entity
@Table(name = "permissions")
public class Permission extends AuditableEntity {
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PermissionName name;
    
    private String description;
    
    public Permission() {
    }
    
    public Permission(PermissionName name) {
        this.name = name;
    }

    public PermissionName getName() {
        return name;
    }

    public void setName(PermissionName name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}