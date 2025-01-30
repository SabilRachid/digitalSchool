package com.digital.school.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "professors")
@PrimaryKeyJoinColumn(name = "user_id") // Relie à la table `users`
public class Professor extends User {

    @ManyToMany
    @JoinTable(
            name = "professor_subjects",
            joinColumns = @JoinColumn(name = "professor_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<Subject> subjects = new HashSet<>(); // Matières enseignées

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }
}
