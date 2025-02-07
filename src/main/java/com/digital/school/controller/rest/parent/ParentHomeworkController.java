package com.digital.school.controller.parent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.User;
import com.digital.school.service.ParentHomeworkService;

import java.util.Map;

@Controller
@RequestMapping("/parent/homework")
public class ParentHomeworkController {

    @Autowired
    private ParentHomeworkService homeworkService;

    @GetMapping
    public String showHomework(@AuthenticationPrincipal User parent, Model model) {
        model.addAttribute("children", homeworkService.getChildrenHomework(parent));
        return "parent/homework";
    }

    @GetMapping("/child/{childId}")
    @ResponseBody
    public ResponseEntity<?> getChildHomework(@PathVariable Long childId) {
        return ResponseEntity.ok(homeworkService.getDetailedChildHomework(childId));
    }

    @GetMapping("/stats/{childId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChildStats(@PathVariable Long childId) {
        return ResponseEntity.ok(homeworkService.getChildHomeworkStats(childId));
    }

    @PostMapping("/{homeworkId}/reminder")
    @ResponseBody
    public ResponseEntity<?> sendReminder(@PathVariable Long homeworkId) {
        try {
            homeworkService.sendHomeworkReminder(homeworkId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
