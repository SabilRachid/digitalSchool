package com.digital.school.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }
}