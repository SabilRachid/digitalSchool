package com.digital.school.controller.rest.parent;

import com.digital.school.model.Parent;
import com.digital.school.service.HomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/parent/homework")
public class ParentHomeworkRestController {

    @Autowired
    private HomeworkService homeworkService;

    /**
     * Affiche la page de gestion des devoirs pour le parent.
     */
    @GetMapping
    public String showHomework(@AuthenticationPrincipal Parent parent, Model model) {
        model.addAttribute("children", homeworkService.getChildrenHomework(parent));
        return "parent/homework";
    }

    /**
     * Retourne les détails d'un devoir pour un enfant spécifique.
     */
    @GetMapping("/child/{childId}")
    @ResponseBody
    public ResponseEntity<?> getChildHomework(@PathVariable Long childId) {
        return ResponseEntity.ok(homeworkService.getDetailedChildHomework(childId));
    }

    /**
     * Retourne les statistiques associées aux devoirs d'un enfant.
     */
    @GetMapping("/stats/{childId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChildStats(@PathVariable Long childId) {
        return ResponseEntity.ok(homeworkService.getChildHomeworkStats(childId));
    }

    /**
     * Envoie un rappel pour un devoir dont la soumission est en attente.
     */
    @PostMapping("/{homeworkId}/reminder")
    @ResponseBody
    public ResponseEntity<?> sendReminder(@PathVariable Long homeworkId) {
        try {
            homeworkService.sendHomeworkReminder(homeworkId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
