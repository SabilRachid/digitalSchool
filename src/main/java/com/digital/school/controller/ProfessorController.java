package com.digital.school.controller;

import com.digital.school.dto.ParticipationDto;
import com.digital.school.model.Homework;
import com.digital.school.service.*;
import com.digital.school.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorController.class);

    @Autowired
    private ProfessorDashboardService dashboardService;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private HomeworkService homeworkService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ParticipationService participationService;

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, @AuthenticationPrincipal User professor, Model model) {
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

        model.addAttribute("currentURI", request.getRequestURI());

        return "professor/dashboard";
    }

    @GetMapping("/dashboard/stats")
    @ResponseBody
    public Map<String, Object> getDashboardStats(@AuthenticationPrincipal User professor) {
        return dashboardService.getProfessorStats(professor);
    }


    /* Affiche la liste des devoirs du professeur */
    @GetMapping("/homeworks")
    public String listHomeworks(@AuthenticationPrincipal User professor, Model model) {
        List<Homework> homeworks = homeworkService.findHomeworksByProfessor(professor);
        model.addAttribute("homeworks", homeworks);
        model.addAttribute("courses", courseService.findByProfessor(professor));
        return "professor/homeworks";
    }

     // Récupérer toutes les participations
    @GetMapping("/participations")
    public String showParticipations(HttpServletRequest request, Model model) {
        List<ParticipationDto> participations = participationService.getAllParticipations().stream()
                .collect(Collectors.toList());
        model.addAttribute("participations", participations);
        model.addAttribute("currentURI", request.getRequestURI());
        return "professor/participations";
    }
    
    
}