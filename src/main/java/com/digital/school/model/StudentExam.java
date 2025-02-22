package com.digital.school.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("exams")
public class StudentExam extends StudentSubmission {

    @ManyToOne(optional = false)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @Column(nullable = false)
    private Boolean isPresent = false;

    // Redéfinition pour renvoyer l'examen associé comme évaluation
    @Override
    public Evaluation getEvaluation() {
        return exam;
    }

    @Override
    public void setEvaluation(Evaluation evaluation) {
        this.exam = (Exam) evaluation;
    }

    // Getters et setters spécifiques

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public Boolean getIsPresent() {
        return isPresent;
    }

    public void setIsPresent(Boolean isPresent) {
        this.isPresent = isPresent;
    }
}
