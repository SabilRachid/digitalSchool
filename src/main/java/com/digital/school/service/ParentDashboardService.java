package com.digital.school.service;

import com.digital.school.model.User;
import java.util.List;
import java.util.Map;


public interface ParentDashboardService {
    List<Map<String, Object>> getChildrenOverview(User parent);
    List<Map<String, Object>> getParentAlerts(User parent);
    Map<String, Object> getParentStats(User parent);
    List<Map<String, Object>> getUpcomingEvents(User parent);
    Map<String, Object> getChildPerformance(Long childId);
    Map<String, Object> getChildAttendance(Long childId);
    List<Map<String, Object>> getChildGrades(Long childId);
    List<Map<String, Object>> getChildHomework(Long childId);
}
