package com.digital.school.controller.rest.admin;

import com.digital.school.service.AdminDashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/dashboard")
public class AdminDashboardRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminDashboardRestController.class);
    @Autowired
    private AdminDashboardService dashboardService;

    //cards stats
    @GetMapping("/stats")
    public Map<String, Object> getAdminStats() {
        return dashboardService.getAdminStats();
    }

    // Chart Répartition des professeus
    @GetMapping("/professors-per-subject")
    public List<Map<String, Object>> getProfessorCountBySubject() {
        List<Map<String, Object>> listMap = dashboardService.getProfessorCountBySubject();
        LOGGER.info("===> result getProfessorCountBySubject " + String.valueOf(listMap.size()));
        return dashboardService.getProfessorCountBySubject();
    }

    // Évolution des Inscriptions
    @GetMapping("/registrations-trend")
    public List<Map<String, Object>> getStudentRegistrationTrend() {
        return dashboardService.getStudentRegistrationTrend();
    }

    // Répartition des utilisateurs
    @GetMapping("/user-distribution")
    public List<Map<String, Object>> getUserDistribution() {
        return dashboardService.getUserDistribution();
    }


    // Performance par Niveau
    @GetMapping("/level-performance")
    public ResponseEntity<List<Map<String, Object>>> getLevelPerformance() {
        List<Map<String, Object>> performanceData = dashboardService.getLevelPerformance();
        return ResponseEntity.ok(performanceData);
    }

    // Taux de Réussite
    @GetMapping("/success-rate")
    public ResponseEntity<List<Map<String, Object>>> getSuccessRate() {
        List<Map<String, Object>> successRateData = dashboardService.getSuccessRate();
        return ResponseEntity.ok(successRateData);
    }

    // Activités Récentes
    @GetMapping("/last-events")
    public ResponseEntity<List<Map<String, Object>>> getLastActivities() {
        List<Map<String, Object>> lastActivitiesData = dashboardService.getLastActivities();
        return ResponseEntity.ok(lastActivitiesData);


    }

}

