package com.digital.school.controller;

import com.digital.school.model.Student;
import com.digital.school.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.digital.school.model.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
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

    @Autowired
    private StudentGradeService gradeService;

    @Autowired
    private ExamService examService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CourseService courseService;

    @Autowired
    private SubjectService subjectService;
    @Autowired
    private StudentHomeworkService studentHomeworkService;

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, @AuthenticationPrincipal Student student, Model model) {
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

                // Current Courses
                model.addAttribute("currentCourses", courseService.findTodaySchedule(student));
                //model.addAttribute("currentCourses", null);

                // Devoirs en attente
                model.addAttribute("pendingHomework", studentHomeworkService.findUpcomingHomeworks(student));

                // Matières et ressources
                model.addAttribute("subjects", dashboardService.getSubjectsWithResources(student));

                // Événements à venir
                model.addAttribute("upcomingEvents", dashboardService.getUpcomingEvents(student));

                model.addAttribute("currentURI", request.getRequestURI());

                return "student/dashboard";
            }
        }
        return "redirect:/login";
    }


    @GetMapping("/homeworks")
    public String homeworks(HttpServletRequest request, @AuthenticationPrincipal Student student, Model model) {
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

                // Devoirs en attente
                model.addAttribute("pendingHomework", studentHomeworkService.findUpcomingHomeworks(student));

                return "student/homeworks";
            }
        }
        return "redirect:/login";
    }


    @GetMapping("/grades")
    public String grades(HttpServletRequest request,
                         @AuthenticationPrincipal Student student,
                         Model model) {
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

                // Récupération séparée des notes de devoirs et d'examens via le service
                List<Map<String, Object>> homeworkGrades = gradeService.findHomeworkGrades(student);
                List<Map<String, Object>> examGrades = gradeService.findExamGrades(student);

                // Ajout au modèle pour utilisation dans le template Thymeleaf
                model.addAttribute("homeworkGrades", homeworkGrades);
                model.addAttribute("examGrades", examGrades);

                // Conversion en JSON pour les graphiques côté client
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JavaTimeModule());
                    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                    String homeworkGradesJson = mapper.writeValueAsString(homeworkGrades);
                    String examGradesJson = mapper.writeValueAsString(examGrades);
                    model.addAttribute("homeworkGradesJson", homeworkGradesJson);
                    model.addAttribute("examGradesJson", examGradesJson);
                } catch (JsonProcessingException e) {
                    LOGGER.error("Erreur lors de la conversion en JSON", e);
                    model.addAttribute("homeworkGradesJson", "[]");
                    model.addAttribute("examGradesJson", "[]");
                }

                model.addAttribute("stats", dashboardService.getStudentStats(student));

                return "student/grades"; // Correspond au template student/grades.html
            }
        }
        return "redirect:/login";
    }


    @GetMapping("/schedules")
    public String studentDashboard(@AuthenticationPrincipal Student student, Model model) {
        model.addAttribute("upcomingExams", examService.findUpcomingExams(student));
        model.addAttribute("upcomingHomeworks", studentHomeworkService.findUpcomingHomeworks(student));
        model.addAttribute("todaySchedule", courseService.findTodaySchedule(student));
        model.addAttribute("nextDaysHomeworks", studentHomeworkService.findHomeworksForNextDays(student));
        return "student/schedules"; // ou "student/grades", selon votre structure
    }


    @GetMapping("/courses")
    public String getStudentCourses(HttpServletRequest request, @AuthenticationPrincipal Student student, Model model) {
        Optional<User> userOptional = userService.findByUsername(student.getUsername());
        if (userOptional.isPresent()) {
            LOGGER.debug("Student user present: {}, URI: {}", student.getUsername(), request.getRequestURI());
            User user = userOptional.get();
            model.addAttribute("user", user);
            model.addAttribute("currentURI", request.getRequestURI());
            // Liste des cours pour l'étudiant
            model.addAttribute("courses", courseService.findByStudent(student));
            // Liste de toutes les matières pour alimenter le filtre
            model.addAttribute("subjects", subjectService.findByStudent(student) );
            return "student/courses"; // Ce template correspond à student/courses.html
        }
        return "redirect:/login";
    }

}