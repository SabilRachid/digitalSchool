package com.digital.school.controller.rest.student;


import com.digital.school.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.digital.school.service.StudentAttendanceService;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/student/absences")
public class StudentAbsenceController {

    @Autowired
    private StudentAttendanceService absenceService;

    @GetMapping
    public String showAbsences(@AuthenticationPrincipal Student student, Model model) {
        model.addAttribute("absences", absenceService.findStudentAbsences(student));
        model.addAttribute("stats", absenceService.getAbsenceStatistics(student));
        return "student/absences";
    }

    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<?> getAbsenceData(
            @AuthenticationPrincipal Student student,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(absenceService.findAbsencesByDateRange(student, start, end));
    }

    @PostMapping("/{id}/justify")
    @ResponseBody
    public ResponseEntity<?> submitJustification(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam String reason,
            @AuthenticationPrincipal Student student) {
        try {
            absenceService.submitJustification(id, file, reason, student);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStatistics(@AuthenticationPrincipal Student student) {
        return ResponseEntity.ok(absenceService.getAbsenceStatistics(student));
    }
}
