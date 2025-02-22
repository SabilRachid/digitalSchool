package com.digital.school.controller.rest.student;

import com.digital.school.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.service.StudentGradeService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student/api/grades")
public class StudentGradesController {

    @Autowired
    private StudentGradeService gradeService;

    @GetMapping("/report")
    public ResponseEntity<?> downloadReport(@AuthenticationPrincipal Student student) {
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
    public ResponseEntity<Map<String, List<Double>>> getProgression(@AuthenticationPrincipal Student student) {
        return ResponseEntity.ok(gradeService.getGradesProgression(student));
    }

    @GetMapping("/rank")
    @ResponseBody
    public ResponseEntity<Integer> getRank(@AuthenticationPrincipal Student student) {
        return ResponseEntity.ok(gradeService.getStudentRank(student));
    }

    @GetMapping("/averages")
    @ResponseBody
    public ResponseEntity<Map<String, Double>> getAverages(@AuthenticationPrincipal Student student) {
        return ResponseEntity.ok(gradeService.getSubjectAverages(student));
    }
}
