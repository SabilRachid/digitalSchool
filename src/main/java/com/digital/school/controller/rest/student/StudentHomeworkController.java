package com.digital.school.controller.rest.student;

import com.digital.school.model.Homework;
import com.digital.school.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.digital.school.model.User;
import com.digital.school.service.HomeworkService;
import com.digital.school.service.SubjectService;

import java.util.Map;

@Controller
@RequestMapping("/student/homework")
public class StudentHomeworkController {

    @Autowired
    private HomeworkService homeworkService;

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public String showHomework(@AuthenticationPrincipal Student student, Model model) {
        model.addAttribute("pendingHomework", homeworkService.findPendingHomework(student));
        model.addAttribute("submittedHomework", homeworkService.findSubmittedHomework(student));
        model.addAttribute("gradedHomework", homeworkService.findGradedHomework(student));
        model.addAttribute("subjects", subjectService.findByStudent(student));
        return "homeworks";
    }

    @GetMapping("/{id}")
    public String viewHomework(@PathVariable Long id, Model model) {
        Homework homework = homeworkService.findById(id)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));
        model.addAttribute("homework", homework);
        return "student/homework-details";
    }

    @PostMapping("/{id}/submit")
    @ResponseBody
    public ResponseEntity<?> submitHomework(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String comment,
            @AuthenticationPrincipal User student) {
        try {
            Homework homework = homeworkService.submitHomework(id, student, file, comment);
            return ResponseEntity.ok(homework);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}/view")
    public ResponseEntity<?> viewSubmission(@PathVariable Long id) {
        try {
            return homeworkService.getSubmissionFile(id);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
