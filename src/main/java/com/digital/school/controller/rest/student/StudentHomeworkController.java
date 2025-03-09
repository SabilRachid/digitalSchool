package com.digital.school.controller.rest.student;

import com.digital.school.model.EvaluationGrade;
import com.digital.school.model.Student;
import com.digital.school.service.StudentHomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/student/homework")
public class StudentHomeworkController {

    @Autowired
    private StudentHomeworkService studentHomeworkService;

    /**
     * Endpoint pour la soumission d'un devoir.
     * Le fichier et un commentaire optionnel sont envoyés en multipart.
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitHomework(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "comment", required = false) String comment,
            @AuthenticationPrincipal Student student) {
        try {
            EvaluationGrade updatedSubmission = studentHomeworkService.submitHomework(id, file, comment, student);
            return ResponseEntity.ok(updatedSubmission);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint pour consulter la soumission d'un devoir.
     */
    @GetMapping("/{id}/view")
    public ResponseEntity<?> viewHomework(
            @PathVariable Long id,
            @AuthenticationPrincipal Student student) {
        try {
            Optional<EvaluationGrade> submission = studentHomeworkService.findByIdAndStudent(id, student);
            return ResponseEntity.ok(submission);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
