package com.digital.school.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "parents")
@PrimaryKeyJoinColumn(name = "user_id")
public class Parent extends User {

    @OneToMany(mappedBy = "parent")
    private Set<Student> children = new HashSet<>(); // Liste des enfants

    public Set<Student> getChildren() {
        return children;
    }

    public void setChildren(Set<Student> children) {
        this.children = children;
    }
}
