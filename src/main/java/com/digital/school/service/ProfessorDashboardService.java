package com.digital.school.service;



import com.digital.school.model.User;
import java.util.Map;

public interface ProfessorDashboardService {
    Map<String, Object> getProfessorStats(User professor);
}