package com.digital.school.controller.rest.parent;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.User;
import com.digital.school.service.ParentDashboardService;

import java.util.Map;

@Controller
@RequestMapping("/parent")
public class ParentDashboardController {

    @Autowired
    private ParentDashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User parent, Model model) {
        // Ajouter les informations des enfants
        model.addAttribute("children", dashboardService.getChildrenOverview(parent));
        
        // Ajouter les alertes et notifications
        model.addAttribute("alerts", dashboardService.getParentAlerts(parent));
        
        // Ajouter les statistiques globales
        model.addAttribute("stats", dashboardService.getParentStats(parent));
        
        // Ajouter les prochains événements
        model.addAttribute("upcomingEvents", dashboardService.getUpcomingEvents(parent));
        
        return "parent/dashboard";
    }

    @GetMapping("/dashboard/stats")
    @ResponseBody
    public Map<String, Object> getStats(@AuthenticationPrincipal User parent) {
        return dashboardService.getParentStats(parent);
    }
}