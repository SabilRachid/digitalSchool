package com.digital.school.service;

import com.digital.school.model.*;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StudentDashboardService {
    StudentDashboardStats getStudentStats(Student student);
    List<StudentSubmission> getRecentGrades(Student student);
    List<Map<String, Object>> getSubjectsWithResources(Student student);
    List<Event> getUpcomingEvents(Student student);
    public Map<String, Object> getStudentStats();
}