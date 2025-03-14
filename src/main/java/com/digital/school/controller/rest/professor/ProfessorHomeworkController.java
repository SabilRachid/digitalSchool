package com.digital.school.controller.rest.professor;

import com.digital.school.dto.HomeworkDTO;
import com.digital.school.model.Homework;
import com.digital.school.model.Professor;
import com.digital.school.service.HomeworkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/professor/api/homeworks")
public class ProfessorHomeworkController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorHomeworkController.class);

    @Autowired
    private HomeworkService homeworkService;

    // Création d'un devoir à partir d'un HomeworkDTO
    @PostMapping
    public ResponseEntity<?> createHomework(@RequestBody HomeworkDTO homeworkDTO,
                                            @AuthenticationPrincipal Professor professor) {
        try {
            LOGGER.debug("Création de devoir : {}", homeworkDTO);
            Homework createdHomework = homeworkService.createHomework(homeworkDTO, professor.getId());
            HomeworkDTO responseDTO = homeworkService.convertToDTO(createdHomework);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors de la création du devoir : " + e.getMessage())
            );
        }
    }

    // Publication d'un devoir
    @PostMapping("/{id}/publish")
    public ResponseEntity<?> publishHomework(@PathVariable Long id) {
        try {
            LOGGER.debug("Publication du devoir, id={}", id);
            homeworkService.publishHomework(id);
            return ResponseEntity.ok(Map.of("message", "Devoir publié avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors de la publication du devoir : " + e.getMessage())
            );
        }
    }

    // Clôture (fin) d'un devoir
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

    // Saisie des notes pour un devoir (entrée d'une note par soumission)
    @PostMapping("/grade-entry")
    public ResponseEntity<?> enterGrade(@RequestBody Map<String, Object> payload,
                                        @AuthenticationPrincipal Professor professor) {
        try {
            LOGGER.debug("Saisie de note pour devoir, payload: {}", payload);
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

    // Récupération des devoirs du professeur avec filtres optionnels
    @GetMapping
    public ResponseEntity<?> getHomeworks(@AuthenticationPrincipal Professor professor,
                                          @RequestParam(required = false) String month,
                                          @RequestParam(required = false) Long classe,
                                          @RequestParam(required = false) Long subject) {
        try {
            LOGGER.debug("Récupération des devoirs avec filtres : month={}, classeId={}, subjectId={}",
                    month, classe, subject);
            List<Homework> homeworks = homeworkService.findHomeworksByProfessor(professor.getId(), month, classe, subject);
            List<HomeworkDTO> dtoList = homeworks.stream()
                    .map(hw -> homeworkService.convertToDTO(hw))
                    .collect(Collectors.toList());
            LOGGER.debug("Liste des devoirs DTO: {}", dtoList);
            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors du chargement des devoirs : " + e.getMessage())
            );
        }
    }

    // Téléchargement du rapport PDF d'un devoir
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

    // Récupération des notes saisies pour un devoir (pour le modal de saisie des notes)
    @GetMapping("/{id}/grades")
    public ResponseEntity<?> getHomeworkGrades(@PathVariable Long id) {
        try {
            LOGGER.debug("Récupération des notes pour le devoir, id={}", id);
            List<Map<String, Object>> gradeEntries = homeworkService.getHomeworkGrades(id);
            return ResponseEntity.ok(gradeEntries);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors de la récupération des notes : " + e.getMessage())
            );
        }
    }
}
