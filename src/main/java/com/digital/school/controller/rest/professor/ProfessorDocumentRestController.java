package com.digital.school.controller.rest.professor;

import com.digital.school.model.Document;
import com.digital.school.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/professor/api/documents")
public class ProfessorDocumentRestController {

    @Autowired
    private DocumentService documentService;

    /**
     * Récupère les documents accessibles aux professeurs.
     */
    @GetMapping("/data")
    public ResponseEntity<List<Map<String, Object>>> getDocumentsData() {
        List<Map<String, Object>> documents = documentService.findAllAsMapForProfessor();
        return ResponseEntity.ok(documents);
    }

    /**
     * Permet au professeur d'uploader une ressource associée à un cours.
     * @param file Le fichier à uploader
     * @param type Le type de document (ex : PDF, VIDEO, LIEN, etc.)
     * @param courseId L'identifiant du cours associé
     * @param description Une description facultative
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam("courseId") Long courseId,
            @RequestParam(value = "description", required = false) String description) {
        try {
            Document doc = documentService.uploadForProfessor(file, type, courseId, description);
            return ResponseEntity.ok(doc);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur lors de l'upload: " + e.getMessage()));
        }
    }

    /**
     * Télécharge le contenu d'un document.
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadDocument(@PathVariable Long id) {
        return documentService.downloadDocument(id);
    }

    /**
     * Met à jour les informations d'un document.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDocument(@PathVariable Long id,
                                            @RequestBody Map<String, Object> resourceData) {
        try {
            Document updated = documentService.update(id, resourceData);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur lors de la mise à jour: " + e.getMessage()));
        }
    }

    /**
     * Supprime un document par son identifiant.
     */
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
