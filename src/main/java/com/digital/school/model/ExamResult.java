package com.digital.school.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_results")
public class ExamResult extends AuditableEntity {
    
    @OneToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;
    
    private Double classAverage;
    
    private Double highestScore;
    
    private Double lowestScore;
    
    @ManyToOne
    @JoinColumn(name = "top_scorer_id")
    private User topScorer;
    
    private Integer totalStudents;
    
    private Integer passedStudents;
    
    private Integer failedStudents;
    
    private LocalDateTime publishedAt;
    
    @ManyToOne
    @JoinColumn(name = "published_by_id")
    private User publishedBy;

    public ExamResult() {
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public Double getClassAverage() {
        return classAverage;
    }

    public void setClassAverage(Double classAverage) {
        this.classAverage = classAverage;
    }

    public Double getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(Double highestScore) {
        this.highestScore = highestScore;
    }

    public Double getLowestScore() {
        return lowestScore;
    }

    public void setLowestScore(Double lowestScore) {
        this.lowestScore = lowestScore;
    }

    public User getTopScorer() {
        return topScorer;
    }

    public void setTopScorer(User topScorer) {
        this.topScorer = topScorer;
    }

    public Integer getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Integer totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Integer getPassedStudents() {
        return passedStudents;
    }

    public void setPassedStudents(Integer passedStudents) {
        this.passedStudents = passedStudents;
    }

    public Integer getFailedStudents() {
        return failedStudents;
    }

    public void setFailedStudents(Integer failedStudents) {
        this.failedStudents = failedStudents;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public User getPublishedBy() {
        return publishedBy;
    }

    public void setPublishedBy(User publishedBy) {
        this.publishedBy = publishedBy;
    }
}