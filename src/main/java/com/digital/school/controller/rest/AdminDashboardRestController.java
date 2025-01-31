package com.digital.school.controller.rest;

import com.digital.school.service.AdminDashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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



    @GetMapping("/professors-per-subject")
    public List<Map<String, Object>> getProfessorCountBySubject() {
        List<Map<String, Object>> listMap = dashboardService.getProfessorCountBySubject();
        LOGGER.info("===> result getProfessorCountBySubject " + String.valueOf(listMap.size()));
        return dashboardService.getProfessorCountBySubject();
    }

    @GetMapping("/registrations-trend")
    public List<Map<String, Object>> getStudentRegistrationTrend() {
        return dashboardService.getStudentRegistrationTrend();
    }

    @GetMapping("/user-distribution")
    public List<Map<String, Object>> getUserDistribution() {
        return dashboardService.getUserDistribution();
    }

    @GetMapping("/stats")
    public Map<String, Object> getAdminStats() {
        return dashboardService.getAdminStats();
    }
}

