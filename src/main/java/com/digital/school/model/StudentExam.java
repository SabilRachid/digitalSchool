package com.digital.school.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("exams")
@Table(name="student_exams")
@PrimaryKeyJoinColumn(name = "id")
public class StudentExam extends StudentSubmission {

    @ManyToOne(optional = false)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @Column(nullable = false)
    private Boolean isPresent = false;

    @Override
    public Evaluation getEvaluation() {
        return exam;
    }

    @Override
    public void setEvaluation(Evaluation evaluation) {
        this.exam = (Exam) evaluation;
    }

    // Getters et setters

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
