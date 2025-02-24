package com.digital.school.controller.rest.professor;

import com.digital.school.model.Homework;
import com.digital.school.model.Professor;
import com.digital.school.service.HomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/professor/api/homeworks")
public class ProfessorHomeworkController {

    @Autowired
    private HomeworkService homeworkService;

    @PostMapping
    public ResponseEntity<?> createHomework(@RequestBody Homework homework,
                                            @AuthenticationPrincipal Professor professor) {
        try {
            Homework createdHomework = homeworkService.createHomework(homework, professor.getId());
            return ResponseEntity.ok(createdHomework);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors de la création du devoir : " + e.getMessage())
            );
        }
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<?> publishHomework(@PathVariable Long id) {
        try {
            homeworkService.publishHomework(id);
            return ResponseEntity.ok(Map.of("message", "Devoir publié avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors de la publication du devoir : " + e.getMessage())
            );
        }
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<?> endHomework(@PathVariable Long id) {
        try {
            homeworkService.endHomework(id);
            return ResponseEntity.ok(Map.of("message", "Devoir terminé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors de la clôture du devoir : " + e.getMessage())
            );
        }
    }

    @PostMapping("/grade-entry")
    public ResponseEntity<?> enterGrade(@RequestBody Map<String, Object> payload,
                                        @AuthenticationPrincipal Professor professor) {
        try {
            Long submissionId = Long.parseLong(payload.get("submissionId").toString());
            Double gradeValue = Double.parseDouble(payload.get("gradeValue").toString());
            String comment = payload.get("comment").toString();
            homeworkService.enterGrade(submissionId, gradeValue, comment, professor.getId());
            return ResponseEntity.ok(Map.of("message", "Notes enregistrées et élève notifié(e)"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors de la saisie des notes : " + e.getMessage())
            );
        }
    }

    @GetMapping
    public ResponseEntity<?> getHomeworks(@AuthenticationPrincipal Professor professor,
                                          @RequestParam(required = false) String month,
                                          @RequestParam(required = false) Long classe,
                                          @RequestParam(required = false) Long subject) {
        try {
            List<Homework> homeworks = homeworkService.findHomeworksByProfessor(professor.getId(), month, classe, subject);
            return ResponseEntity.ok(homeworks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors du chargement des devoirs : " + e.getMessage())
            );
        }
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<?> downloadHomeworkReport(@PathVariable Long id) {
        try {
            byte[] pdfContent = homeworkService.generateHomeworkReport(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=homework-report-" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfContent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors du téléchargement du rapport : " + e.getMessage())
            );
        }
    }
}
