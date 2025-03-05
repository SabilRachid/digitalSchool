package com.digital.school.dto;

import java.time.LocalDateTime;

public class StudentAttendanceDTO {

    private Long studentId;
    private String studentFirstName;
    private String studentLastName;
    private String status;         // "PRESENT", "ABSENT", "RETARD", etc.
    private LocalDateTime recordedAt;
    private String justification;  // Texte de justification, si applicable
    private String justificationFile; // Chemin ou nom du fichier justificatif

    public StudentAttendanceDTO() {
    }

    public StudentAttendanceDTO(Long studentId, String studentFirstName, String studentLastName, String status,
                                LocalDateTime recordedAt, String justification, String justificationFile) {
        this.studentId = studentId;
        this.studentFirstName = studentFirstName;
        this.studentLastName = studentLastName;
        this.status = status;
        this.recordedAt = recordedAt;
        this.justification = justification;
        this.justificationFile = justificationFile;
    }

    // Getters and Setters

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentFirstName() {
        return studentFirstName;
    }

    public void setStudentFirstName(String studentFirstName) {
        this.studentFirstName = studentFirstName;
    }

    public String getStudentLastName() {
        return studentLastName;
    }

    public void setStudentLastName(String studentLastName) {
        this.studentLastName = studentLastName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
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

    @Override
    public String toString() {
        return "StudentAttendanceDTO{" +
                "studentId=" + studentId +
                ", studentFirstName='" + studentFirstName + '\'' +
                ", studentLastName='" + studentLastName + '\'' +
                ", status='" + status + '\'' +
                ", recordedAt=" + recordedAt +
                ", justification='" + justification + '\'' +
                ", justificationFile='" + justificationFile + '\'' +
                '}';
    }
}
