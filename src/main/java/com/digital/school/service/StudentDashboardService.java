package com.digital.school.service;

import com.digital.school.model.*;
import java.util.List;
import java.util.Map;

public interface StudentDashboardService {
    StudentDashboardStats getStudentStats(User student);
    List<StudentGrade> getRecentGrades(User student);
    List<Homework> getPendingHomework(User student);
    List<Map<String, Object>> getSubjectsWithResources(User student);
    List<Event> getUpcomingEvents(User student);
}