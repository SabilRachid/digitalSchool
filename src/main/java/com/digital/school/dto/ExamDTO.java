package com.digital.school.dto;

import com.digital.school.model.enumerated.EvaluationStatus;
import java.time.LocalDateTime;
import java.time.LocalDate;

public class ExamDTO {

    private Long id;
    private String title;             // Titre de l'examen
    private String description;      // Description de l'examen
    private LocalDateTime startTime; // Date et heure de début de l'examen
    private int duration;            // Durée en minutes
    private Long roomId;         // Nom de la salle (optionnel)
    private LocalDate examDate;      // Date de l'examen (peut être dérivée de startTime)
    private Long subjectId;
    private Long classeId;
    private String professorName;    // Nom de la classe
    private EvaluationStatus status; // Statut de l'examen (SCHEDULED, IN_PROGRESS, COMPLETED)
    private double maxScore;
    private boolean graded;
    // Score maximum
    // Constructeur vide
    public ExamDTO() {
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        // Si examDate n'est pas défini, on peut le déduire du startTime
        if (startTime != null && this.examDate == null) {
            this.examDate = startTime.toLocalDate();
        }
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public Long getClasseId() {
        return classeId;
    }

    public void setClasseId(Long classeId) {
        this.classeId = classeId;
    }

    public EvaluationStatus getStatus() {
        return status;
    }

    public void setStatus(EvaluationStatus status) {
        this.status = status;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }

    public boolean isGraded() {
        return graded;
    }

    public void setGraded(boolean graded) {
        this.graded = graded;
    }


    @Override
    public String toString() {
        return "ExamDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", roomId=" + roomId +
                ", examDate=" + examDate +
                ", subjectId=" + subjectId +
                ", classeId=" + classeId +
                ", professorName='" + professorName + '\'' +
                ", status=" + status +
                ", maxScore=" + maxScore +
                ", graded=" + graded +
                '}';
    }
}
