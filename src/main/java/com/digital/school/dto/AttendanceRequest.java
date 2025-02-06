package com.digital.school.dto;


import java.time.LocalDate;
import java.util.Map;

public class AttendanceRequest {

    private Long classId;
    private LocalDate date;
    private Map<Long, String> attendances; // Clé: Student ID, Valeur: "PRESENT" / "RETARD" / "ABSENT"

    public AttendanceRequest() {
    }

    public AttendanceRequest(Long classId, LocalDate date, Map<Long, String> attendances) {
        this.classId = classId;
        this.date = date;
        this.attendances = attendances;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Map<Long, String> getAttendances() {
        return attendances;
    }

    public void setAttendances(Map<Long, String> attendances) {
        this.attendances = attendances;
    }
}

