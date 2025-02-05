package com.digital.school.service;

import com.digital.school.model.enumerated.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.digital.school.model.Attendance;
import com.digital.school.model.Course;
import com.digital.school.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AttendanceService {
    Page<Attendance> findAll(Pageable pageable);
    List<Attendance> findAll();
    Optional<Attendance> findById(Long id);
    Attendance save(Attendance attendance);
    void save(List<Attendance> attendanceList);
    void deleteById(Long id);
    List<Attendance> findByStudent(User student);
    List<Attendance> findByCourse(Course course);
    List<Attendance> findByStudentAndDateRange(User student, LocalDateTime start, LocalDateTime end);
    List<Attendance> findByCourseAndDateRange(Course course, LocalDateTime start, LocalDateTime end);
    double getAttendanceRate(User student, Course course);
    Map<String, Double> getClassAttendanceStats(Course course);
    List<Attendance> getAttendancesByCourseAndDate(Course course, LocalDate date);
    void updateAttendance(Long id, AttendanceStatus status);
}