package com.digital.school.controller;

import com.digital.school.dto.ParticipationDto;
import com.digital.school.model.Exam;
import com.digital.school.model.Homework;
import com.digital.school.model.Professor;
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
    private ClasseService classeService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private HomeworkService homeworkService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ParticipationService participationService;

    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private ExamService examService;

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, @AuthenticationPrincipal Professor professor, Model model) {
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
    public Map<String, Object> getDashboardStats(@AuthenticationPrincipal Professor professor) {
        return dashboardService.getProfessorStats(professor);
    }


    /* Affiche la liste des devoirs du professeur */
    @GetMapping("/homeworks")
    public String listHomeworks(@AuthenticationPrincipal Professor professor, Model model) {
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

    @GetMapping("/attendances")
    public String listAttendances(HttpServletRequest request,  @AuthenticationPrincipal Professor professor, Model model) {

        model.addAttribute("teacherId", professor.getId());
        model.addAttribute("classes", classeService.getAll());
        model.addAttribute("currentURI", request.getRequestURI());
        return "professor/attendances";
    }

    @GetMapping("/courses")
    public String getCoursesForProfessor(HttpServletRequest request,  @AuthenticationPrincipal Professor professor, Model model) {

        LOGGER.debug("📌 Requête reçue : {}", request.getRequestURI());
        model.addAttribute("teacherId", professor.getId());
        List<Map<String, Object>> courses = courseService.findByProfessor(professor);
        return "professor/courses";
    }


    @GetMapping("/exams")
    public String getExamsForProfessor(HttpServletRequest request, @AuthenticationPrincipal Professor professor, Model model) {
        // Récupérer la liste des examens pour le professeur connecté via le service
        List<Exam> exams = examService.findExamsByProfessor(professor.getId());
        model.addAttribute("exams", exams);
        model.addAttribute("teacherId", professor.getId());

        // Vous pouvez ajouter d'autres attributs nécessaires pour le template
        model.addAttribute("currentURI", request.getRequestURI());

        // Retourner le nom du template (par exemple, "professor/exams")
        return "professor/exams";
    }

    @GetMapping("/grades")
    public String getGradesForProfessor(HttpServletRequest request,
                                        @AuthenticationPrincipal Professor professor,
                                        Model model) {
        // Par exemple, récupérer la liste des classes et matières concernées par le professeur
        model.addAttribute("teacherId", professor.getId());
        model.addAttribute("classes", classeService. findByProfessor(professor));
        model.addAttribute("subjects", subjectService.findByProfessor(professor));
        model.addAttribute("currentURI", request.getRequestURI());
        return "professor/grades"; // Correspond au template Thymeleaf (ex: templates/professor/grades.html)
    }


    @GetMapping("/resources")
    public String getJustificationPage(HttpServletRequest request,
                                       @AuthenticationPrincipal Professor professor,
                                       Model model) {
        model.addAttribute("teacherId", professor.getId());
        model.addAttribute("currentURI", request.getRequestURI());
        return "professor/resources"; // Renvoie vers la template justification.html
    }

    @GetMapping("/meetings")
    public String getMeetingPage(HttpServletRequest request,
                                       @AuthenticationPrincipal Professor professor,
                                       Model model) {
        model.addAttribute("teacherId", professor.getId());
        model.addAttribute("currentURI", request.getRequestURI());
        return "professor/meetings"; // Renvoie vers la template justification.html
    }



}

