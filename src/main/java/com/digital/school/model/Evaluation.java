package com.digital.school.model;

import com.digital.school.model.enumerated.EvaluationStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "evaluations")
@DiscriminatorValue("evaluations")
@PrimaryKeyJoinColumn(name = "id")
public abstract class Evaluation extends Event {

    @Column(name = "due_date")
    private LocalDate dueDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "professor_id")
    private Professor professor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EvaluationStatus status;

    @Column(name = "maxScore", nullable = true)
    private Double maxScore;

    // Nouveau champ pour indiquer si l'évaluation est notée
    @Column(name = "graded", nullable = true)
    private boolean graded = false;

    // Getters and setters
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
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

    public boolean isGraded() {
        return graded;
    }

    public void setGraded(boolean graded) {
        this.graded = graded;
    }

    @Override
    public String toString() {
        return "Evaluation{" +
                ", professorUserName=" + professor.getUsername() +
                ", status=" + status.name() +
                ", maxScore=" + maxScore +
                ", graded=" + graded +
                '}';
    }
}
