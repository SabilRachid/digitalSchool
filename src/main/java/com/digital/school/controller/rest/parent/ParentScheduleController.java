package com.digital.school.controller.rest.parent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.User;
import com.digital.school.service.ParentScheduleService;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/parent/schedule")
public class ParentScheduleController {

    @Autowired
    private ParentScheduleService scheduleService;

    @GetMapping
    public String showSchedule(@AuthenticationPrincipal User parent, Model model) {
        model.addAttribute("children", scheduleService.getChildrenSchedule(parent));
        return "parent/schedule";
    }

    @GetMapping("/child/{childId}")
    @ResponseBody
    public ResponseEntity<?> getChildSchedule(
            @PathVariable Long childId,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(scheduleService.getChildSchedule(childId, start, end));
    }

    @GetMapping("/events/{childId}")
    @ResponseBody
    public ResponseEntity<?> getChildEvents(@PathVariable Long childId) {
        return ResponseEntity.ok(scheduleService.getChildEvents(childId));
    }

    @GetMapping("/stats/{childId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChildStats(@PathVariable Long childId) {
        return ResponseEntity.ok(scheduleService.getChildScheduleStats(childId));
    }
}
