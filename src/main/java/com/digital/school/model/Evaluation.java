package com.digital.school.model;

import com.digital.school.model.enumerated.EvaluationStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "evaluations")
@DiscriminatorColumn(name="evaluation_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Evaluation extends AuditableEntity {

    private String name;

    @Column(nullable = false)
    private String title;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne(optional = false)
    @JoinColumn(name = "professor_id")
    private Professor professor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "class_id")
    private Classe classe;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EvaluationStatus status;

    @Column(name = "maxScore", nullable = true)
    private Double maxScore;

    // Getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public EvaluationStatus getStatus() {
        return status;
    }

    public void setStatus(EvaluationStatus status) {
        this.status = status;
    }

    public Double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }

    @Override
    public String toString() {
        return "Evaluation{" +
                ", title='" + title + '\'' +
                ", dueDate=" + dueDate +
                ", subjectName=" + subject.getName() +
                ", professorUserName=" + professor.getUsername() +
                ", classeName=" + classe.getName() +
                ", status=" + status.name() +
                ", maxScore=" + maxScore +
                '}';
    }
}
