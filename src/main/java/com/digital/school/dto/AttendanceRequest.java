package com.digital.school.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Map;

/**
 * DTO pour enregistrer les statuts d'attendance des étudiants pour un cours.
 * Ce DTO sert à mettre à jour ou créer les enregistrements de StudentAttendance
 * dans l'entité Attendance associée au cours.
 */
public class AttendanceRequest {

    @NotNull(message = "attendanceId ne peut pas être nul")
    private Long attendanceId; // facultatif

    @NotNull(message = "classId ne peut pas être nul")
    private Long classId;

    @NotNull(message = "courseId ne peut pas être nul")
    private Long courseId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "date ne peut pas être nulle")
    private LocalDate date;

    /**
     * Map où chaque clé représente l'ID d'un étudiant (sous forme de chaîne)
     * et la valeur le statut d'attendance ("PRESENT", "ABSENT", "RETARD", etc.).
     */
    private Map<String, String> attendances;

    public AttendanceRequest() {
    }

    public AttendanceRequest(Long classId, Long courseId, LocalDate date, Map<String, String> attendances) {
        this.classId = classId;
        this.courseId = courseId;
        this.date = date;
        this.attendances = attendances;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Map<String, String> getAttendances() {
        return attendances;
    }

    public void setAttendances(Map<String, String> attendances) {
        this.attendances = attendances;
    }

    public Long getAttendanceId() { return attendanceId; }

    public void setAttendanceId(Long attendanceId) { this.attendanceId = attendanceId; }

    @Override
    public String toString() {
        return "AttendanceRequest{" +
                "attendanceId=" + attendanceId +
                ", courseId=" + courseId +
                ", date=" + date +
                ", attendances=" + attendances +
                '}';
    }


}
