package com.digital.school.service.impl;


import com.digital.school.model.Resource;
import com.digital.school.model.Subject;
import com.digital.school.model.Professor;
import com.digital.school.repository.ResourceRepository;
import com.digital.school.repository.SubjectRepository;
import com.digital.school.repository.ProfessorRepository;
import com.digital.school.service.ResourceService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Implémentation du service de gestion des ressources pédagogiques.
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final SubjectRepository subjectRepository;
    private final ProfessorRepository professorRepository;

    @Value("${app.storage.resources}")  // Définir le répertoire de stockage dans application.properties
    private String resourceStoragePath;

    public ResourceServiceImpl(ResourceRepository resourceRepository, SubjectRepository subjectRepository,
                               ProfessorRepository professorRepository) {
        this.resourceRepository = resourceRepository;
        this.subjectRepository = subjectRepository;
        this.professorRepository = professorRepository;
    }

    @Override
    public Resource addResource(String title, String description, MultipartFile file, Long subjectId, Long professorId) {
        try {
            // Vérification du sujet et du professeur
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Matière non trouvée"));

            Professor professor = professorRepository.findById(professorId)
                    .orElseThrow(() -> new RuntimeException("Professeur non trouvé"));

            // Enregistrement du fichier sur le serveur
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(resourceStoragePath, fileName);
            Files.write(filePath, file.getBytes());

            // Création et sauvegarde de la ressource
            Resource resource = new Resource();
            resource.setTitle(title);
            resource.setDescription(description);
            resource.setUrl(filePath.toString());
           // resource.setSubject(subject);
           // resource.setProfessor(professor);

            return resourceRepository.save(resource);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du fichier", e);
        }
    }

    @Override
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    @Override
    public List<Resource> getResourcesBySubject(Long subjectId) {
        return resourceRepository.findBySubjectId(subjectId);
    }

    @Override
    public List<Resource> getResourcesByProfessor(Long professorId) {
        return resourceRepository.findByProfessorId(professorId);
    }

    @Override
    public void deleteResource(Long resourceId) {
        resourceRepository.deleteById(resourceId);
    }
}
