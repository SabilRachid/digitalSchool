package com.digital.school.service;

import com.digital.school.dto.AttendanceRequest;
import com.digital.school.model.*;
import com.digital.school.model.enumerated.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AttendanceService {
    Page<Attendance> findAll(Pageable pageable);
    List<Attendance> findAll();
    Optional<Attendance> findById(Long id);

    List<Attendance> findByStudent(User student);
    List<Attendance> findByCourse(Course course);

    Map<String, Object> getGroupedAttendanceData(Professor professor, Long classId, LocalDate startDate, LocalDate endDate);
    Optional<Attendance> findByIdAndTeacher(Long id, Long teacherId);

    Attendance save(Attendance attendance);
    void saveAttendance(AttendanceRequest request);
    void save(List<Attendance> attendanceList);

    void deleteById(Long id);
    double getAttendanceRate(Student student, Course course);
    Map<String, Double> getClassAttendanceStats(Course course);
    List<Attendance> getAttendancesByCourseAndDate(Course course, LocalDate date);
    void updateAttendance(Long id, AttendanceStatus status);
    boolean isTeacherAllowedToModify(Long teacherId, Long courseId);
    boolean existsById(Long id);
    List<Attendance> getAbsenceStatistics(Student student);
    List<Attendance> getAbsenceStatistics();

    ResponseEntity<?> getJustificationFile(Long id);
}