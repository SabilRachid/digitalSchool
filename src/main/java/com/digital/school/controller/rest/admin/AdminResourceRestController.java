package com.digital.school.controller.rest.admin;

import com.digital.school.model.Document;
import com.digital.school.service.DocumentService;
import com.digital.school.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/resources")
public class AdminResourceRestController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserService userService;

    @GetMapping("/data")
    public List<Map<String, Object>> getDocumentsData() {
        return documentService.findAllAsMap();
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam("category") String category,
            @RequestParam(value = "studentId", required = false) Long studentId,
            @RequestParam(value = "parentId", required = false) Long parentId) {
        try {
            Document document = documentService.upload(file, type, category,
                    null, // uploader : à définir via le contexte si nécessaire
                    studentId != null ? new com.digital.school.model.Student() {{ setId(studentId); }} : null,
                    parentId != null ? new com.digital.school.model.Parent() {{ setId(parentId); }} : null);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur lors de l'upload: " + e.getMessage()));
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadDocument(@PathVariable Long id) {
        try {
            return documentService.downloadDocument(id);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur lors du téléchargement: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/validate")
    public ResponseEntity<?> validateDocument(@PathVariable Long id) {
        try {
            Document document = documentService.validate(id);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur lors de la validation: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        try {
            documentService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur lors de la suppression: " + e.getMessage()));
        }
    }
}
