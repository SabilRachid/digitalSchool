package com.digital.school.dto;

import java.time.LocalDate;

public class AttendanceDTO {
    private Long id;
    private String courseName;
    private LocalDate date;
    private int studentAttendanceCount;

    // Getters et setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCourseName() {
        return courseName;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public int getStudentAttendanceCount() {
        return studentAttendanceCount;
    }
    public void setStudentAttendanceCount(int studentAttendanceCount) {
        this.studentAttendanceCount = studentAttendanceCount;
    }

    @Override
    public String toString() {
        return "AttendanceDTO{" +
                "id=" + id +
                ", courseName='" + courseName + '\'' +
                ", date=" + date +
                ", studentAttendanceCount=" + studentAttendanceCount +
                '}';
    }
}
