package com.digital.school.service;

import java.util.List;
import java.util.Map;

public interface AdminDashboardService {

    //nombre de professeurs par matière
    public List<Map<String, Object>> getProfessorCountBySubject();

    //tendance des inscriptions
    List<Map<String, Object>> getStudentRegistrationTrend();

    //répartition Utilisateurs
    List<Map<String, Object>> getUserDistribution();

    Map<String, Object> getAdminStats();

}
