package com.digital.school.model;

import com.digital.school.model.enumerated.ParticipationType;
import com.digital.school.model.enumerated.ParticipationLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "participations")
public class Participation extends AuditableEntity {


    /*Suivre la participation aux cours (présence active, interactions, questions posées...)
    Évaluer la participation aux travaux de groupe
    Donner un feedback sur l'engagement des étudiants dans la classe*/
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student; // Étudiant concerné

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Cours concerné



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationLevel level; // Niveau de participation

    @Enumerated(EnumType.STRING)
    @Column(name = "participation_type", nullable = false)
    private ParticipationType participationType;

    @Column(columnDefinition = "TEXT")
    private String feedback; // Remarque sur la participation

    private LocalDateTime recordedAt = LocalDateTime.now(); // Date d'enregistrement

    public Participation() {}

    // Getters et Setters
    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public ParticipationLevel getLevel() {
        return level;
    }

    public void setLevel(ParticipationLevel level) {
        this.level = level;
    }

    public ParticipationType getParticipationType() {
        return participationType;
    }

    public void setParticipationType(ParticipationType participationType) {
        this.participationType = participationType;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}

