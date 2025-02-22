package com.digital.school.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("exams")
public class Exam extends Evaluation {

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    // Durée de l'examen en minutes
    @Column(name = "duration", nullable = false)
    private int duration;

    // Salle ou emplacement de l'examen
    @Column(name = "room")
    private String room;

    // Getters et setters existants...

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

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    // Méthode dérivée pour obtenir la date de l'examen (basée sur startTime)
    public LocalDate getDate() {
        return startTime.toLocalDate();
    }
}
