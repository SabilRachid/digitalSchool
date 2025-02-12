package com.digital.school.controller;

import com.digital.school.model.Administrator;
import com.digital.school.model.enumerated.RoleName;
import com.digital.school.service.ClasseService;
import com.digital.school.service.ParentService;
import com.digital.school.service.StudentService;
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
import com.digital.school.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private ParentService parentService;

    @Autowired
    private ClasseService classeService;


    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
    // Créer une méthode pour calculer la somme des nombres pairs dans une liste
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !(authentication instanceof AnonymousAuthenticationToken)) {
            
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUsername(username);
            
            if (userOptional.isPresent()) {
                LOGGER.debug("Admin user present: {}"+ username + ", URI: " +request.getRequestURI());
                User user = userOptional.get();
                model.addAttribute("user", user);
                
                model.addAttribute("currentURI", request.getRequestURI());

                return "admin/dashboard";
            }
        }
        return "redirect:/login";
    }


    @GetMapping("/classes")
    public String showClasses(HttpServletRequest request, Model model) {
        LOGGER.debug("showClasses called " + getClass().getName());
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/classes";
    }

    @GetMapping("/courses")
    public String showCourses(HttpServletRequest request, Model model) {
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/courses";
    }

    @GetMapping("/documents")
    public String showDocuments(HttpServletRequest request,  Model model) {
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/documents";
    }

    @GetMapping("/levels")
    public String showLevels(HttpServletRequest request, Model model) {
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/levels";
    }

    @GetMapping("/profiles")
    public String showProfiles(HttpServletRequest request, Model model) {
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/profiles";
    }

    @GetMapping("/rooms")
    public String showRooms(HttpServletRequest request, Model model) {
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/rooms";
    }

    @GetMapping("/subjects")
    public String showSubjects(HttpServletRequest request, Model model) {
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/subjects";
    }

    @GetMapping("/resources")
    public String showResources(HttpServletRequest request, Model model) {
        LOGGER.debug("showResources Controller");
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/resources";
    }



    @GetMapping("/users")
    public String listUsers(HttpServletRequest request, Model model) {
        model.addAttribute("roles", RoleName.values());
        model.addAttribute("classes", classeService.findAllBasicInfo());
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/users";
    }

    @GetMapping("/parentStudent")
    public String listAttendances(HttpServletRequest request, @AuthenticationPrincipal Administrator admin, Model model) {

        model.addAttribute("user", admin);
        model.addAttribute("classes", classeService.findAllBasicInfo());
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("parents", parentService.findAll());
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/parentStudent";
    }


}