package com.digital.school.controller.rest.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.User;
import com.digital.school.service.StudentGradeService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student/grades")
public class StudentGradesController {

    @Autowired
    private StudentGradeService gradeService;

    @GetMapping
    public String showGrades(@AuthenticationPrincipal User student, Model model) {
        model.addAttribute("gradesBySubject", gradeService.findGradesBySubject(student));
        model.addAttribute("stats", gradeService.calculatePerformanceStats(student));
        return "student/grades";
    }

    @GetMapping("/report")
    public ResponseEntity<?> downloadReport(@AuthenticationPrincipal User student) {
        try {
            byte[] report = gradeService.generateReport(student);
            return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=bulletin.pdf")
                .body(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/progression")
    @ResponseBody
    public ResponseEntity<Map<String, List<Double>>> getProgression(@AuthenticationPrincipal User student) {
        return ResponseEntity.ok(gradeService.getGradesProgression(student));
    }

    @GetMapping("/rank")
    @ResponseBody
    public ResponseEntity<Integer> getRank(@AuthenticationPrincipal User student) {
        return ResponseEntity.ok(gradeService.getStudentRank(student));
    }

    @GetMapping("/averages")
    @ResponseBody
    public ResponseEntity<Map<String, Double>> getAverages(@AuthenticationPrincipal User student) {
        return ResponseEntity.ok(gradeService.getSubjectAverages(student));
    }
}
