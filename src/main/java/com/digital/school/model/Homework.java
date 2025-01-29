package com.digital.school.model;

import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "homework")
public class Homework extends AuditableEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private User professor;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false) // 🔥 Ajout du lien avec un cours
    private Course course;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "status", nullable = false)
    private String status;

    public Homework() {
    }

    // Getters and Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public User getProfessor() {
        return professor;
    }

    public void setProfessor(User professor) {
        this.professor = professor;
    }

    public Course getCourse() { // ✅ Getter pour le cours
        return course;
    }

    public void setCourse(Course course) { // ✅ Setter pour le cours
        this.course = course;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}