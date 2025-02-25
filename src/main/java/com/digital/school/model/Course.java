package com.digital.school.model;

import jakarta.persistence.*;
import com.digital.school.model.enumerated.CourseStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
public class Course extends AuditableEntity {

    // Nom du cours (obligatoire)
    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Classe classe;

    // Date du cours (facultatif)
    private LocalDate date;

    // Horaires du cours
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Statut du cours (par exemple : SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status;

    // Salle physique du cours
    private String room;

    // Nombre de ressources associées (ex. documents, liens, etc.)
    @Column(name = "resource_count", nullable = true)
    private int resourceCount = 0;

    // Lien en ligne pour les cours virtuels (si applicable)
    private String onlineLink;

    // Description détaillée du cours
    @Column(columnDefinition = "TEXT")
    private String description;

    // Raison d'annulation (si le cours est annulé)
    @Column(columnDefinition = "TEXT")
    private String cancellationReason;

    // Notes de l'instructeur
    @Column(columnDefinition = "TEXT")
    private String instructorNotes;

    // Nouvelle propriété online avec valeur par défaut à false
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean online = false;

    public Course() {
    }

    // Getters et setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getResourceCount() {
        return resourceCount;
    }

    public void setResourceCount(int resourceCount) {
        this.resourceCount = resourceCount;
    }

    public String getOnlineLink() {
        return onlineLink;
    }

    public void setOnlineLink(String onlineLink) {
        this.onlineLink = onlineLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getInstructorNotes() {
        return instructorNotes;
    }

    public void setInstructorNotes(String instructorNotes) {
        this.instructorNotes = instructorNotes;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
