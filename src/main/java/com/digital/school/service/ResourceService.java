package com.digital.school.service;

import com.digital.school.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des ressources pédagogiques.
 */
public interface ResourceService {

    // Ajouter une ressource pédagogique avec un fichier
    Resource addResource(String title, String description, MultipartFile file, Long subjectId, Long professorId);

    // Récupérer toutes les ressources pédagogiques
    List<Resource> getAllResources();

    // Récupérer les ressources par matière
    List<Resource> getResourcesBySubject(Long subjectId);

    // Récupérer les ressources d'un professeur
    List<Resource> getResourcesByProfessor(Long professorId);

    // Supprimer une ressource par ID
    void deleteResource(Long resourceId);
}
