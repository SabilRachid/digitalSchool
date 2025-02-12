package com.digital.school.controller.rest.secretary;


import com.digital.school.model.Parent;
import com.digital.school.service.ParentDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/secretary")
public class SecretaryDashboardController {

    @Autowired
    private ParentDashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal Parent parent, Model model) {
        // Ajouter les informations des enfants
        return "secretary/dashboard";
    }

    @GetMapping("/dashboard/stats")
    @ResponseBody
    public Map<String, Object> getStats(@AuthenticationPrincipal Parent parent) {
        return dashboardService.getParentStats(parent);
    }
}