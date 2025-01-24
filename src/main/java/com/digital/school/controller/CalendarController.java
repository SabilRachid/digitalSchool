package com.digital.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.digital.school.model.*;
import com.digital.school.service.EventService;

@Controller
public class CalendarController {

    @Autowired
    private EventService eventService;

    @GetMapping("/calendar")
    public String showCalendar(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", user);
        return "calendar";
    }
}