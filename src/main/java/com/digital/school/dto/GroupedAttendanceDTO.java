package com.digital.school.dto;

import java.time.LocalDate;

public class GroupedAttendanceDTO {

    private Long courseId;
    private String courseName;
    private String className;
    private LocalDate date;
    private Long count;

    public GroupedAttendanceDTO() {
    }

    public GroupedAttendanceDTO(Long courseId, String courseName, String className, LocalDate date, Long count) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.className = className;
        this.date = date;
        this.count = count;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "GroupedAttendanceDTO{" +
                "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", className='" + className + '\'' +
                ", date=" + date +
                ", count=" + count +
                '}';
    }
}
