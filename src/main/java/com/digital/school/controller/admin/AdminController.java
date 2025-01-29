package com.digital.school.controller.admin;

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
@RequestMapping("/admin")
public class AdminController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;
    
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
    
    
    @GetMapping("/dashboard/stats")
    @ResponseBody
    public Map<String, Object> getAdminStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUsername(username);
            if (userOptional.isPresent()) {
                return userService.getAdminDashboardStats();
            }
        }
        return null;
    }
    
    
    

}