package com.digital.school.controller.student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.digital.school.model.User;
import com.digital.school.service.StudentDashboardService;
import com.digital.school.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private StudentDashboardService dashboardService;
    
    
    
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User student, HttpServletRequest request, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !(authentication instanceof AnonymousAuthenticationToken)) {
            
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUsername(username);
            
            if (userOptional.isPresent()) {
                LOGGER.debug("Student user present: {}, URI: {}", username, request.getRequestURI());
                User user = userOptional.get();
                model.addAttribute("user", user);
                model.addAttribute("currentURI", request.getRequestURI());
                
                // Statistiques générales
                model.addAttribute("stats", dashboardService.getStudentStats(student));
                
                // Notes récentes
                model.addAttribute("recentGrades", dashboardService.getRecentGrades(student));
                
                // Devoirs en attente
                model.addAttribute("pendingHomework", dashboardService.getPendingHomework(student));
                
                // Matières et ressources
                model.addAttribute("subjects", dashboardService.getSubjectsWithResources(student));
                
                // Événements à venir
                model.addAttribute("upcomingEvents", dashboardService.getUpcomingEvents(student));
                
                return "student/dashboard";
            }
        }
        return "redirect:/login";
    }
    

    @GetMapping("/student/dashboard/stats")
    @ResponseBody
    public Map<String, Object> getStudentStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUsername(username);
            if (userOptional.isPresent()) {
                return userService.getStudentDashboardStats(userOptional.get());
            }
        }
        return null;
    }

    @GetMapping("/dashboard/statss")
    @ResponseBody
    public ResponseEntity<?> getStats(@AuthenticationPrincipal User student) {
        try {
            return ResponseEntity.ok(dashboardService.getStudentStats(student));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}