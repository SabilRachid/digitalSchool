package com.digital.school.controller.rest.professor;

import com.digital.school.service.ProfessorDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/professor/api/dashboard")
public class ProfessorDashboardRestController {

    @Autowired
    private ProfessorDashboardService professorDashboardService;

    @GetMapping("/class-performance")
    public ResponseEntity<List<Map<String, Object>>> getClassPerformance() {
        return ResponseEntity.ok(professorDashboardService.getClassPerformance());
    }

    @GetMapping("/grades-distribution")
    public ResponseEntity<List<Map<String, Object>>> getGradesDistribution() {
        return ResponseEntity.ok(professorDashboardService.getGradesDistribution());
    }

    @GetMapping("/participation-rate")
    public ResponseEntity<Map<String, Integer>> getParticipationRate() {
        return ResponseEntity.ok(professorDashboardService.getParticipationRate());
    }

    @GetMapping("/average-progression")
    public ResponseEntity<List<Map<String, Object>>> getAverageProgression() {
        return ResponseEntity.ok(professorDashboardService.getAverageProgression());
    }
}

