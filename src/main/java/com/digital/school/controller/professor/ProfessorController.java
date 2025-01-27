package com.digital.school.controller.professor;

import com.digital.school.service.CourseService;
import com.digital.school.service.MessageService;
import com.digital.school.service.ProfessorDashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.digital.school.model.User;
import com.digital.school.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorController.class);

    @Autowired
    private ProfessorDashboardService dashboardService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private HomeworkService homeworkService;

    @Autowired
    private MessageService messageService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User professor, Model model) {
        // Add user to model
        model.addAttribute("user", professor);

        // Add dashboard statistics
        model.addAttribute("stats", dashboardService.getProfessorStats(professor));

        // Add today's schedule
        model.addAttribute("todaySchedule", courseService.findTodaySchedule(professor));

        // Add pending homework
        model.addAttribute("pendingHomework", homeworkService.findPendingGradingByProfessor(professor));

        // Add recent messages
        model.addAttribute("recentMessages", messageService.findRecentMessages(professor, 5));

        return "professor/dashboard";
    }

    @GetMapping("/dashboard/stats")
    @ResponseBody
    public Map<String, Object> getDashboardStats(@AuthenticationPrincipal User professor) {
        return dashboardService.getProfessorStats(professor);
    }


    @GetMapping("/dashboardd/stats")
    @ResponseBody
    public Map<String, Object> getProfessorStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUsername(username);
            if (userOptional.isPresent()) {
                return userService.getProfessorDashboardStats(userOptional.get());
            }
        }
        return null;
    }
    
    
    
}