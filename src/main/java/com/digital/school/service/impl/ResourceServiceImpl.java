package com.digital.school.service.impl;


import com.digital.school.dto.ResourceDto;
import com.digital.school.model.Course;
import com.digital.school.model.Resource;
import com.digital.school.repository.ResourceRepository;
import com.digital.school.service.ResourceService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceServiceImpl(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    // 📌 Ajouter une ressource
    @Override
    public Resource addResource(Resource resource) {
        return resourceRepository.save(resource);
    }


    // ✅ Conversion DTO → Entité avec association du `Course`
    public static Resource toEntity(ResourceDto dto, Course course) {
        Resource resource = new Resource();
        resource.setId(dto.getId());
        resource.setTitle(dto.getTitle());
        resource.setType(dto.getType());
        resource.setUrl(dto.getUrl());
        resource.setDescription(dto.getDescription());
        resource.setCourse(course); // Associe le cours récupéré
        return resource;
    }

    @Override
    public Resource saveResource(Resource resource) {
        return resourceRepository.save(resource);

    }

    // 📌 Modifier une ressource (vérification de l'existence)
    @Override
    public Resource updateResource(Long resourceId, Resource resource) {
        Resource existingResource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Ressource introuvable avec ID: " + resourceId));

        // Mise à jour des données
        existingResource.setTitle(resource.getTitle());
        existingResource.setType(resource.getType());
        existingResource.setUrl(resource.getUrl());
        existingResource.setCourse(resource.getCourse());
        existingResource.setDescription(resource.getDescription());

        return resourceRepository.save(existingResource);
    }

    // 📌 Supprimer une ressource
    @Override
    public void deleteResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Ressource introuvable avec ID: " + resourceId));
        resourceRepository.delete(resource);
    }

    // 📌 Récupérer toutes les ressources (Admins uniquement)
    @Override
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    // 📌 Récupérer les ressources liées à un cours
    @Override
    public List<Resource> getResourcesByCourse(Long courseId) {
        return resourceRepository.findByCourseId(courseId);
    }

    // 📌 Vérifier si un professeur est propriétaire de la ressource
    @Override
    public boolean isResourceOwner(Long resourceId, Long professorId) {
        return resourceRepository.existsByIdAndCourse_Professor_Id(resourceId, professorId);
    }

    @Override
    public List<Map<String, Object>> findAllAsMap() {
        //resoudre ce pb
        return resourceRepository.findAll().stream()
                .map(resource -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", resource.getId());
                    map.put("title", resource.getTitle());
                    map.put("type", resource.getType());
                    map.put("url", resource.getUrl());
                    map.put("course", resource.getCourse().getName());
                    map.put("description", resource.getDescription());
                    return map;
                })
                .toList();
    }
}
