package com.digital.school.controller.rest;

import com.digital.school.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/dashboard")
public class AdminDashboardRestController {

    @Autowired
    private AdminDashboardService dashboardService;



    @GetMapping("/professors-per-subject")
    public List<Map<String, Object>> getProfessorCountBySubject() {
        return dashboardService.getProfessorCountBySubject();
    }
}

