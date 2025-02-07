package com.digital.school.controller.rest.secretary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.service.DocumentService;
import com.digital.school.service.ClasseService;
import com.digital.school.service.UserService;
import com.digital.school.service.BulletinService;

import java.util.Map;

@Controller
@RequestMapping("/secretary/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private BulletinService bulletinService;

    @Autowired
    private ClasseService classeService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String showDocuments(Model model) {
        model.addAttribute("bulletins", bulletinService.findAll());
        model.addAttribute("certificates", documentService.findByType("CERTIFICATE"));
        model.addAttribute("attestations", documentService.findByType("ATTESTATION"));
        model.addAttribute("classes", classeService.findAllBasicInfo());
        model.addAttribute("students", userService.findByRole(RoleName.ROLE_STUDENT));
        return "secretary/documents";
    }

    @PostMapping("/generate")
    @ResponseBody
    public ResponseEntity<?> generateDocument(@RequestBody Map<String, Object> request) {
        try {
            String type = (String) request.get("type");
            switch (type) {
                case "BULLETIN":
                    return generateBulletin(request);
                case "CERTIFICATE":
                    return generateCertificate(request);
                case "ATTESTATION":
                    return generateAttestation(request);
                default:
                    throw new IllegalArgumentException("Type de document invalide");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/bulletins/generate")
    @ResponseBody
    public ResponseEntity<?> generateBulletins() {
        try {
            bulletinService.generateAllBulletins();
            return ResponseEntity.ok()
                    .body(Map.of("message", "Génération des bulletins lancée"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadDocument(@PathVariable Long id) {
        try {
            return documentService.downloadDocument(id);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<?> previewDocument(@PathVariable Long id) {
        try {
            return documentService.previewDocument(id);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/send")
    @ResponseBody
    public ResponseEntity<?> sendDocument(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            documentService.sendDocument(id, request);
            return ResponseEntity.ok()
                    .body(Map.of("message", "Document envoyé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    private ResponseEntity<?> generateBulletin(Map<String, Object> request) {
        Long classeId = Long.parseLong(request.get("classeId").toString());
        String period = (String) request.get("period");
        String title = (String) request.get("title");
        String comments = (String) request.get("comments");

        return ResponseEntity.ok(bulletinService.generateBulletin(
                classeId, period, title, comments));
    }

    private ResponseEntity<?> generateCertificate(Map<String, Object> request) {
        Long studentId = Long.parseLong(request.get("studentId").toString());
        String title = (String) request.get("title");
        String comments = (String) request.get("comments");

        return ResponseEntity.ok(documentService.generateCertificate(
                studentId, title, comments));
    }

    private ResponseEntity<?> generateAttestation(Map<String, Object> request) {
        Long studentId = Long.parseLong(request.get("studentId").toString());
        String title = (String) request.get("title");
        String comments = (String) request.get("comments");

        return ResponseEntity.ok(documentService.generateAttestation(
                studentId, title, comments));
    }
}


