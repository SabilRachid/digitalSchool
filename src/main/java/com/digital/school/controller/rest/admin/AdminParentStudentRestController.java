package com.digital.school.controller.rest.admin;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.digital.school.service.ParentStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/admin/api/parentStudent")
public class AdminParentStudentRestController {


        @Autowired
        private ParentStudentService parentStudentService;

    // Récupérer toutes les associations (optionnellement filtrées par classe)
    @GetMapping("/associations/data")
    @ResponseBody
    public Map<String, Object> getAssociations(@RequestParam(required = false) Long classId) {
        List<Map<String, Object>> associations = parentStudentService.getAssociationsByClass(classId);
        return Collections.singletonMap("data", associations);
    }

    // Récupérer une association par ID (pour modification)
    @GetMapping("/association/{id}")
    public ResponseEntity<Map<String, Object>> getAssociation(@PathVariable Long id) {
        Map<String, Object> association = parentStudentService.getAssociationById(id);
        return ResponseEntity.ok(association);
    }

    // Création d'une nouvelle association
    @PostMapping("/association")
    public ResponseEntity<?> createAssociation(@RequestBody Map<String, Object> associationData) {
        try {
            parentStudentService.saveAssociation(associationData);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    // Modification d'une association existante
    @PutMapping("/association/{id}")
    public ResponseEntity<?> updateAssociation(@PathVariable Long id, @RequestBody Map<String, Object> associationData) {
        try {
            parentStudentService.updateAssociation(id, associationData);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

        @PostMapping("/associate")
        public String associateStudentWithParents(
                @RequestParam("studentId") Long studentId,
                @RequestParam("parentIds") List<Long> parentIds,
                RedirectAttributes redirectAttributes) {

            try {
                parentStudentService.associateStudentToParents(studentId, parentIds);
                redirectAttributes.addFlashAttribute("successMessage", "Association enregistrée avec succès.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'association : " + e.getMessage());
            }
            return "redirect:/admin/parentStudent/associate"; // Redirige vers la page d'association ou dashboard
        }

}
