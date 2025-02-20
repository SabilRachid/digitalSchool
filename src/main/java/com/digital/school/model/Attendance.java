package com.digital.school.model;

import jakarta.persistence.*;
import com.digital.school.model.enumerated.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendances")
public class Attendance extends AuditableEntity {
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    @Column(nullable = false)
    private LocalDate dateEvent;

    private LocalDateTime recordedAt = LocalDateTime.now();
    
    @Column(columnDefinition = "TEXT")
    private String justification;

    // Chemin ou nom de fichier du justificatif téléchargé
    @Column(name = "justification_file")
    private String justificationFile;
    
    @ManyToOne
    @JoinColumn(name = "recorded_by_id")
    private User recordedBy;

    public Attendance() {
    }

      public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public LocalDate getDateEvent() {
        return dateEvent;
    }

    public void setDateEvent(LocalDate dateEvent) {
        this.dateEvent = dateEvent;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getJustificationFile() {
        return justificationFile;
    }
    public void setJustificationFile(String justificationFile) {
        this.justificationFile = justificationFile;
    }

    public User getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(User recordedBy) {
        this.recordedBy = recordedBy;
    }
}