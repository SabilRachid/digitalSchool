package com.digital.school.dto;

import com.digital.school.model.enumerated.EvaluationStatus;
import java.time.LocalDate;

public class HomeworkDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Long subjectId;
    private Long classeId;
    private String professorName;
    private EvaluationStatus status;
    private boolean graded;

    // Constructeurs
    public HomeworkDTO() {}

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

    public LocalDate getDueDate() {
        return dueDate;
    }
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Long getSubjectId() {
        return subjectId;
    }
    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
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

    public String getProfessorName() {
        return professorName;
    }
    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public boolean isGraded() { return graded; }
    public void setGraded(boolean graded) { this.graded = graded;}

    @Override
    public String toString() {
        return "HomeworkDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", subjectId=" + subjectId +
                ", classeId=" + classeId +
                ", professorName='" + professorName + '\'' +
                ", status=" + status +
                ", graded=" + graded +
                '}';
    }
}
