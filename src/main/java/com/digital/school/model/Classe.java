package com.digital.school.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "classes")
public class Classe extends AuditableEntity {
    

    
    @Column(nullable = false)
    private String name;
    
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;
    
    private Integer maxStudents;
    
    @Column(name = "school_year")
    private String schoolYear;
    
    @OneToMany(mappedBy = "classe")
    private Set<Student> students = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "class_subjects",
        joinColumns = @JoinColumn(name = "class_id"),
        inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<Subject> subjects = new HashSet<>();


    @ManyToMany(mappedBy = "classes") // Relation bidirectionnelle avec Professor
    private Set<Professor> professors = new HashSet<>();

    public Classe() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Integer getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students != null ? students : new HashSet<>();
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects != null ? subjects : new HashSet<>();
    }

    public
    Set<Professor> getProfessors() {
        return professors;
    }
    public void setProfessors(Set<Professor> professors) {
        this.professors = professors != null ? professors : new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Classe classe = (Classe) o;
        return id != null && id.equals(classe.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}