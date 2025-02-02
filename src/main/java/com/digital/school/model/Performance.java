package com.digital.school.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "performances")
public class Performance extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student; // L'étudiant concerné

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = true)
    private Subject subject; // Matière concernée (optionnel si c'est une moyenne générale)

    private Double averageGrade; // Moyenne des notes obtenues

    private Double classAverage; // Moyenne générale de la classe pour comparaison

    private Integer rank; // Classement de l'étudiant

    private Integer totalStudents; // Nombre total d'étudiants dans la classe

    private LocalDateTime updatedAt; // Date de mise à jour de la performance

    public Performance(Student student) {
        this.updatedAt = LocalDateTime.now();
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Double getAverageGrade() {
        return averageGrade;
    }

    public void setAverageGrade(Double averageGrade) {
        this.averageGrade = averageGrade;
    }

    public Double getClassAverage() {
        return classAverage;
    }

    public void setClassAverage(Double classAverage) {
        this.classAverage = classAverage;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Integer totalStudents) {
        this.totalStudents = totalStudents;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
