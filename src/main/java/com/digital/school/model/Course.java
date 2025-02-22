package com.digital.school.model;

import jakarta.persistence.*;
import com.digital.school.model.enumerated.CourseStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
public class Course extends AuditableEntity {

    //ajoute propriété name
    private String name;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
    
    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private User professor;
    
    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Classe classe;

    // Nouvelle propriété pour la date du cours
    @Column(nullable = true)
    private LocalDate date;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    // Champ status utilisant l'Enum CourseStatus
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status;

    private String room;

    @Column(name = "resource_count", nullable = true)
    private int resourceCount=0;

    private String onlineLink;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    public Course() {
    }


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

    public User getProfessor() {
        return professor;
    }

    public void setProfessor(User professor) {
        this.professor = professor;
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public LocalDate getDate() {return date;}

    public void setDate(LocalDate date) {this.date = date;}

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

    public String getRoom() {
        return room;
    }

    public CourseStatus getStatus() {return status;}

    public void setStatus(CourseStatus status) {this.status = status;}

    public void setRoom(String room) {this.room = room;}

    public int getResourceCount() {return resourceCount;}

    public void setResourceCount(int resourceCount) {this.resourceCount = resourceCount;}

    public String getOnlineLink() {return onlineLink; }

    public void setOnlineLink(String onlineLink) { this.onlineLink = onlineLink;}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}