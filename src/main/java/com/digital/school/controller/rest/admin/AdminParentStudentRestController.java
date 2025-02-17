package com.digital.school.controller.rest.admin;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.digital.school.service.ParentStudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/admin/api/parentStudent")
public class AdminParentStudentRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminParentStudentRestController.class);

        @Autowired
        private ParentStudentService parentStudentService;

    // Récupérer toutes les associations (optionnellement filtrées par classe)
    @GetMapping("/association/data")
    @ResponseBody
    public List<Map<String, Object>> getAssociations(@RequestParam(required = false) Long classId) {
        LOGGER.debug("Datas getAssociations of ParentStudent");
        List<Map<String, Object>> associations = parentStudentService.getAssociationsByClass(classId);
        return associations;
    }

    // Récupérer une association par ID (pour modification)
    @GetMapping("/association/{id}")
    public ResponseEntity<Map<String, Object>> getAssociation(@PathVariable Long id) {
        LOGGER.debug("getAssociation of ParentStudent id="+id);
        Map<String, Object> association = parentStudentService.getAssociationById(id);
        return ResponseEntity.ok(association);
    }


    // Modification d'une association existante
    @PutMapping("/association/{id}")
    public ResponseEntity<?> updateAssociation(@PathVariable Long id, @RequestBody Map<String, Object> associationData) {

        try {
            LOGGER.debug("updateAssociation of ParentStudent id="+id);
            parentStudentService.updateAssociation(id, associationData);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @DeleteMapping("/association/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteAssociation(@PathVariable Long id) {
        try {
            LOGGER.debug("deleteAssociation of ParentStudent id="+id);
            parentStudentService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    // Création d'une nouvelle association
    @PostMapping("/association")
    public ResponseEntity<?> createAssociation(@RequestBody Map<String, Object> associationData) {

        try {
            LOGGER.debug("createAssociation of ParentStudent");
            parentStudentService.saveAssociation(associationData);
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
                LOGGER.debug("associateStudentWithParents studentId="+studentId+" parentIds="+parentIds);
                parentStudentService.associateStudentToParents(studentId, parentIds);
                redirectAttributes.addFlashAttribute("successMessage", "Association enregistrée avec succès.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'association : " + e.getMessage());
            }
            return "redirect:/admin/parentStudent/associate"; // Redirige vers la page d'association ou dashboard
        }



}
