package com.digital.school.dto;

import java.time.LocalDateTime;

public class CourseDTO {
    private Long subject;
    private Long professor;
    private Long classe;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String room;
    private String description;

    // Getters et Setters à générer

    public Long getSubject() {
        return subject;
    }

    public void setSubject(Long subject) {
        this.subject = subject;
    }

    public Long getProfessor() {
        return professor;
    }

    public void setProfessor(Long professor) {
        this.professor = professor;
    }

    public Long getClasse() {
        return classe;
    }

    public void setClasse(Long classe) {
        this.classe = classe;
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

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
