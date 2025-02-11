package com.digital.school.model;

import jakarta.persistence.*;
import com.digital.school.model.enumerated.ExamStatus;
import java.time.LocalDateTime;

@Entity
@Table(name = "exams")
public class Exam extends AuditableEntity {
    

    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime examDate;
    
    @Column(nullable = false)
    private Integer duration;
    
    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Classe classe;
    
    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
    
    @Column(nullable = false)
    private Double maxScore;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamStatus status = ExamStatus.SCHEDULED;
    
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private Boolean isPublished = false;


   private LocalDateTime publishedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "room_id")
   private Room room;

    public Exam() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDateTime examDate) {
        this.examDate = examDate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }

    public ExamStatus getStatus() {
        return status;
    }

    public void setStatus(ExamStatus status) {
        this.status = status;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}