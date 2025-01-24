package com.digital.school.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.digital.school.model.Document;
import com.digital.school.service.DocumentService;
import com.digital.school.service.UserService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/documents")
public class AdminDocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String showDocuments(Model model) {
        return "admin/documents";
    }

    @GetMapping("/data")
    @ResponseBody
    public List<Map<String, Object>> getDocumentsData() {
        return documentService.findAllAsMap();
    }

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam("category") String category,
            @RequestParam(value = "studentId", required = false) Long studentId,
            @RequestParam(value = "parentId", required = false) Long parentId) {
        try {
            Document document = documentService.upload(file, type, category, studentId, parentId);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de l'upload: " + e.getMessage()));
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadDocument(@PathVariable Long id) {
        try {
            return documentService.download(id);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors du téléchargement: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/validate")
    @ResponseBody
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
    @ResponseBody
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