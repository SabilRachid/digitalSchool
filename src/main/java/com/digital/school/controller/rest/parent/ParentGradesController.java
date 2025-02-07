package com.digital.school.controller.rest.parent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.User;
import com.digital.school.service.ParentGradeService;

@Controller
@RequestMapping("/parent/grades")
public class ParentGradesController {

    @Autowired
    private ParentGradeService gradeService;

    @GetMapping
    public String showGrades(@AuthenticationPrincipal User parent, Model model) {
        model.addAttribute("children", gradeService.getChildrenGrades(parent));
        return "parent/grades";
    }

    @GetMapping("/child/{childId}")
    @ResponseBody
    public ResponseEntity<?> getChildGrades(@PathVariable Long childId) {
        return ResponseEntity.ok(gradeService.getDetailedChildGrades(childId));
    }

    @GetMapping("/report/{childId}")
    public ResponseEntity<?> downloadReport(@PathVariable Long childId) {
        try {
            byte[] report = gradeService.generateChildReport(childId);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=bulletin.pdf")
                    .body(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/stats/{childId}")
    @ResponseBody
    public ResponseEntity<?> getChildStats(@PathVariable Long childId) {
        return ResponseEntity.ok(gradeService.getChildGradeStats(childId));
    }
}