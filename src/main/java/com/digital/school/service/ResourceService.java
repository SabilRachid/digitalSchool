package com.digital.school.service;

import com.digital.school.model.Resource;
import com.digital.school.dto.ResourceDto;
import java.util.List;
import java.util.Map;

public interface ResourceService {

    // 📌 Ajouter une ressource (Admins & Professeurs uniquement)
    Resource addResource(Resource resource);

    // 📌 Ajouter une ressource (Admins & Professeurs uniquement)
    ResourceDto saveResource(Resource resource);

    // 📌 Modifier une ressource (Admins & Professeurs ayant créé la ressource)
    Resource updateResource(Long resourceId, Resource resource);

    // 📌 Supprimer une ressource (Admins & Profs propriétaires)
    void deleteResource(Long resourceId);

    // 📌 Récupérer toutes les ressources (Admins uniquement)
    List<Resource> getAllResources();

    // 📌 Récupérer les ressources d'un cours (Professeurs et Étudiants inscrits)
    List<Resource> getResourcesByCourse(Long courseId);

    // 📌 Vérifier si un professeur est propriétaire d’une ressource
    boolean isResourceOwner(Long resourceId, Long professorId);

    List<Map<String, Object>> findAllAsMap();
}
