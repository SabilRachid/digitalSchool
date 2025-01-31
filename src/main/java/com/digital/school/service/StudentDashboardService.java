package com.digital.school.service;

import com.digital.school.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StudentDashboardService {
    StudentDashboardStats getStudentStats(User student);
    List<StudentGrade> getRecentGrades(User student);
    List<Homework> getPendingHomework(User student);
    List<Map<String, Object>> getSubjectsWithResources(User student);
    List<Event> getUpcomingEvents(User student);
    public Map<String, Object> getStudentStats();
}