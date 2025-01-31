package com.digital.school.model;

import com.digital.school.model.enumerated.EmploymentType;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = true)
    private EmploymentType employmentType; // Temps plein ou temps partiel

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }
}
