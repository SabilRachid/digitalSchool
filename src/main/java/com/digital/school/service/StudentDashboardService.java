package com.digital.school.service;

import com.digital.school.model.*;

import java.util.List;
import java.util.Map;

public interface StudentDashboardService {
    StudentDashboardStats getStudentStats(Student student);
    List<EvaluationGrade> getRecentGrades(Student student);
    List<Map<String, Object>> getSubjectsWithResources(Student student);
    List<Event> getUpcomingEvents(Student student);
    public Map<String, Object> getStudentStats();
}