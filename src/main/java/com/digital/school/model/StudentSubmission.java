package com.digital.school.model;

import com.digital.school.model.enumerated.GradeType;
import com.digital.school.model.enumerated.StudentSubmissionStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Student_Submissions")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "submission_type", discriminatorType = DiscriminatorType.STRING)
public abstract class StudentSubmission extends AuditableEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "evaluation_id")
    private Evaluation evaluation;

    // Date de soumission (pour examen ou devoir)
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    // Remarques ou commentaires généraux
    @Column(columnDefinition = "TEXT")
    private String comments;

    @ManyToOne
    @JoinColumn(name = "graded_by_id")
    private User gradedBy;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

    @Column(name = "grade_value")
    private Double value;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade_letter", nullable = false)
    private GradeType gradeType;

    // Champ status pour la soumission (par exemple, PENDING, COMPLETED, LATE, etc.)
    @Enumerated(EnumType.STRING)
    @Column(name = "submission_status", nullable = false)
    private StudentSubmissionStatus status;


    // Getters & setters

    public StudentSubmission() {}

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public User getGradedBy() {
        return gradedBy;
    }

    public void setGradedBy(User gradedBy) {
        this.gradedBy = gradedBy;
    }

    public LocalDateTime getGradedAt() {
        return gradedAt;
    }

    public void setGradedAt(LocalDateTime gradedAt) {
        this.gradedAt = gradedAt;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public GradeType getGradeType() {
        return gradeType;
    }

    public void setGradeType(GradeType gradeType) {
        this.gradeType = gradeType;
    }

    public StudentSubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(StudentSubmissionStatus status) {
        this.status = status;
    }

    // Méthode utilitaire (optionnelle)
    public Object getClassAverage() {
        return null;
    }
}
