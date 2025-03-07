package com.digital.school.controller;

import com.digital.school.model.Parent;
import com.digital.school.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@Controller
@RequestMapping("/parent")
public class ParentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParentController.class);

    @Autowired
    private ParentDashboardService parentDashboardService;

    @Autowired
    private UserService userService;


    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, @AuthenticationPrincipal Parent parent, Model model) {

        model.addAttribute("user", parent);
        // Add user to model
        model.addAttribute("children", parentDashboardService.getChildrenOverview(parent));
        // Ajouter les alertes et notifications
        model.addAttribute("alerts", parentDashboardService.getParentAlerts(parent));
        // Ajouter les statistiques globales
        model.addAttribute("stats", parentDashboardService.getParentStats(parent));
        // Ajouter les prochains événements
        model.addAttribute("upcomingEvents", parentDashboardService.getUpcomingEvents(parent));
        return "parent/dashboard";
    }

    @GetMapping("/dashboard/stats")
    @ResponseBody
    public Map<String, Object> getStats(@AuthenticationPrincipal Parent parent) {
        return parentDashboardService.getParentStats(parent);
    }





}
