package com.digital.school.service;


import com.digital.school.model.Attendance;
import com.digital.school.model.Document;
import com.digital.school.model.Student;
import com.digital.school.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface StudentAbsenceService {
    List<Attendance> findStudentAbsences(Student student);
    List<Attendance> findAbsencesByDateRange(Student student, LocalDateTime start, LocalDateTime end);
    Document submitJustification(Long absenceId, MultipartFile file, String reason, Student student);
    Map<String, Object> getAbsenceStatistics(Student student);
    boolean isAbsenceJustified(Long absenceId);
}
