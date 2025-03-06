package com.digital.school.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("exams")
@Table(name = "exams")
@PrimaryKeyJoinColumn(name = "id")
public class Exam extends Evaluation {

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    // Durée de l'examen en minutes
    @Column(name = "duration", nullable = false)
    private int duration;

    // Au lieu d'une chaîne, on utilise une relation vers la classe Room
    @ManyToOne(optional = true)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Getters et setters

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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    // Méthode dérivée pour obtenir la date de l'examen (basée sur startTime)
    public LocalDate getDate() {
        return startTime.toLocalDate();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;

    }
}
