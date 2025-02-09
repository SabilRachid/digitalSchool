package com.digital.school.service;



import com.digital.school.model.Professor;
import com.digital.school.model.User;

import java.util.List;
import java.util.Map;

public interface ProfessorDashboardService {

    // Dashboard statistics for the professor
    Map<String, Object> getProfessorStats(Professor professor);

    // Charts data for the professor dashboard
    List<Map<String, Object>> getClassPerformance(); // Moyenne des notes par classe
    List<Map<String, Object>> getGradesDistribution(); // Répartition des notes
    Map<String, Integer> getParticipationRate(); // Taux de participation des élèves
    List<Map<String, Object>> getAverageProgression(); // Progression des moyennes sur 6 mois
}