package com.digital.school.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.*;
import com.digital.school.service.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Optional;

@Controller
public class DashboardController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private AttendanceService attendanceService;
    
    @Autowired
    private CourseService courseService;

    @GetMapping({"/dashboard"})
    public String dashboard(HttpServletRequest request, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !(authentication instanceof AnonymousAuthenticationToken)) {
            
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUsername(username);
            
            if (userOptional.isPresent()) {
                LOGGER.debug("User present: {}", userOptional.get());
                User user = userOptional.get();
                model.addAttribute("user", user);
                model.addAttribute("currentURI", request.getRequestURI());
                

                if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                   // return "redirect:/admin/dashboard";
                	return "redirect:/admin/dashboard";
                } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR"))) {
                    return "redirect:/professor/dashboard";
                } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
                    return "redirect:/student/dashboard";
                }
                else  return "dashboard";
            }     
        }
        else {
                return "redirect:/login";
        }
		return null;
            
    }

    
   

    
}