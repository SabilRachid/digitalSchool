package com.digital.school.service.impl;

import com.digital.school.model.Document;
import com.digital.school.model.Parent;
import com.digital.school.model.Student;
import com.digital.school.model.User;
import com.digital.school.model.enumerated.DocumentType;
import com.digital.school.repository.DocumentRepository;
import com.digital.school.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    // MÉTHODES GÉNÉRALES

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> findAllAsMap() {
        return documentRepository.findAll().stream()
                .map(doc -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", doc.getId());
                    map.put("title", doc.getName());
                    map.put("type", doc.getType());
                    map.put("description", doc.getDescription());
                    // Ajoutez d'autres champs utiles si nécessaire
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }

    @Override
    public Document save(Document document) {
        return documentRepository.save(document);
    }

    @Override
    public Document upload(MultipartFile file, String type, String category, User uploader, Student student, Parent parent) {
        try {
            Document document = new Document();
            document.setName(file.getOriginalFilename());
            document.setType(DocumentType.valueOf(type.toUpperCase()));
            document.setDescription(category);
            document.setFileContent(file.getBytes());
            document.setContentType(file.getContentType());
            document.setOwner(uploader);
            if (student != null) {
                document.setRelatedEntityId(student.getId());
                document.setRelatedEntityType("Student");
            } else if (parent != null) {
                document.setRelatedEntityId(parent.getId());
                document.setRelatedEntityType("Parent");
            }
            return documentRepository.save(document);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'upload de la ressource", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> downloadDocument(Long id) {
        Optional<Document> optDoc = documentRepository.findById(id);
        if (optDoc.isPresent()) {
            Document document = optDoc.get();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(document.getFileContent());
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Document non trouvé"));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> previewDocument(Long id) {
        Optional<Document> optDoc = documentRepository.findById(id);
        if (optDoc.isPresent()) {
            Document document = optDoc.get();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(document.getContentType()))
                    .body(document.getFileContent());
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Document non trouvé"));
        }
    }

    @Override
    public Document validate(Long id) {
        Optional<Document> optDoc = documentRepository.findById(id);
        if (optDoc.isPresent()) {
            Document doc = optDoc.get();
            // Implémentez ici la logique de validation (par exemple, modification d'un statut)
            return documentRepository.save(doc);
        } else {
            throw new RuntimeException("Document non trouvé pour validation");
        }
    }

    @Override
    public Document validate(Long id, User validator) {
        Optional<Document> optDoc = documentRepository.findById(id);
        if (optDoc.isPresent()) {
            Document doc = optDoc.get();
            // Implémentez ici la logique de validation avec le validateur (ex: doc.setValidated(true); doc.setValidatedBy(validator))
            return documentRepository.save(doc);
        } else {
            throw new RuntimeException("Document non trouvé pour validation");
        }
    }

    @Override
    public void deleteById(Long id) {
        documentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Document> findAll() {
        return documentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Document> findByStudent(Student student) {
        return documentRepository.findByRelatedEntityIdAndRelatedEntityType(student.getId(), "Student");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Document> findByParent(Parent parent) {
        return documentRepository.findByRelatedEntityIdAndRelatedEntityType(parent.getId(), "Parent");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Document> findByCategory(String category) {
        return documentRepository.findByDescriptionContaining(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Document> findByType(String type) {
        return documentRepository.findByType(DocumentType.valueOf(type.toUpperCase()));
    }

    @Override
    @Transactional(readOnly = true)
    public Document findByStudentAndType(Student student, String type) {
        return documentRepository.findFirstByRelatedEntityIdAndRelatedEntityTypeAndType(
                student.getId(), "Student", DocumentType.valueOf(type.toUpperCase())
        ).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByStudentAndType(Student student, String type) {
        return documentRepository.existsByRelatedEntityIdAndRelatedEntityTypeAndType(
                student.getId(), "Student", DocumentType.valueOf(type.toUpperCase())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByParentAndType(Parent parent, String type) {
        return documentRepository.existsByRelatedEntityIdAndRelatedEntityTypeAndType(
                parent.getId(), "Parent", DocumentType.valueOf(type.toUpperCase())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Document> findByParentAndType(Parent parent, String type) {
        return documentRepository.findByRelatedEntityIdAndRelatedEntityTypeAndType(
                parent.getId(), "Parent", DocumentType.valueOf(type.toUpperCase())
        );
    }


    @Override
    public Document update(Long id, Map<String, Object> resourceData) {
        Optional<Document> optionalDoc = documentRepository.findById(id);
        if (!optionalDoc.isPresent()) {
            throw new RuntimeException("Document not found for id: " + id);
        }
        Document doc = optionalDoc.get();

        // Mettre à jour le titre si présent
        if (resourceData.containsKey("title")) {
            doc.setName(resourceData.get("title").toString());
        }

        // Mettre à jour le type s'il est présent et valide
        if (resourceData.containsKey("type")) {
            try {
                String typeStr = resourceData.get("type").toString().toUpperCase();
                doc.setType(DocumentType.valueOf(typeStr));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Type de document invalide: " + resourceData.get("type"));
            }
        }

        // Mettre à jour la description si présente
        if (resourceData.containsKey("description")) {
            doc.setDescription(resourceData.get("description").toString());
        }

        // Mettre à jour l'URL du fichier si présente
        if (resourceData.containsKey("fileUrl")) {
            doc.setFileUrl(resourceData.get("fileUrl").toString());
        }

        // Mettre à jour le Content-Type si présent
        if (resourceData.containsKey("contentType")) {
            doc.setContentType(resourceData.get("contentType").toString());
        }

        // Mettre à jour l'association à une entité liée si présente
        if (resourceData.containsKey("relatedEntityId")) {
            try {
                Long relatedEntityId = Long.parseLong(resourceData.get("relatedEntityId").toString());
                doc.setRelatedEntityId(relatedEntityId);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid relatedEntityId value: " + resourceData.get("relatedEntityId"));
            }
        }
        if (resourceData.containsKey("relatedEntityType")) {
            doc.setRelatedEntityType(resourceData.get("relatedEntityType").toString());
        }

        return documentRepository.save(doc);
    }


    // MÉTHODES SPÉCIFIQUES POUR L'USAGE DES RESSOURCES (Admin/Professor)

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> findAllAsMapForProfessor() {
        List<Document> docs = documentRepository.findAll();
        return docs.stream().map(doc -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", doc.getId());
            map.put("title", doc.getName());
            map.put("type", doc.getType());
            map.put("courseName",
                    (doc.getRelatedEntityType() != null && doc.getRelatedEntityType().equals("Course") && doc.getRelatedEntityId() != null)
                            ? "Nom du cours" // À remplacer par la récupération effective du nom du cours
                            : "-");
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public Document uploadForProfessor(MultipartFile file, String type, Long courseId, String description) {
        try {
            Document document = new Document();
            document.setName(file.getOriginalFilename());
            document.setType(DocumentType.valueOf(type.toUpperCase()));
            document.setDescription(description);
            document.setFileContent(file.getBytes());
            document.setContentType(file.getContentType());
            // Associer le document au cours via relatedEntityId/type
            document.setRelatedEntityId(courseId);
            document.setRelatedEntityType("Course");
            return documentRepository.save(document);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'upload de la ressource", e);
        }
    }


    // MÉTHODES DE GÉNÉRATION DE DOCUMENTS
    @Override
    public Object generateCertificate(Long studentId, String title, String comments) {
        // Implémentez la génération du certificat (par exemple en PDF)
        return null;
    }

    @Override
    public Object generateAttestation(Long studentId, String title, String comments) {
        // Implémentez la génération de l'attestation
        return null;
    }

    @Override
    public void sendDocument(Long id, Map<String, Object> request) {
        // Implémentez la logique d'envoi du document (par email, etc.)
    }
}
