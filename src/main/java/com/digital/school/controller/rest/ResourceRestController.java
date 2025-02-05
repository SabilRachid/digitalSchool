package com.digital.school.controller.rest;

import com.digital.school.dto.ResourceDto;
import com.digital.school.model.Course;
import com.digital.school.model.Resource;
import com.digital.school.service.CourseService;
import com.digital.school.service.ResourceService;
import com.digital.school.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

//donne moi une interface html pour afficher et gérer les
@RestController
@RequestMapping("/admin/api/resources")
@PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'STUDENT', 'PARENT')")
public class ResourceRestController {


    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceRestController.class);

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private CourseService courseService;



    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<List<ResourceDto>> getResources() {
        LOGGER.debug("getResources api return ResourceDto");
        List<ResourceDto> resources = resourceService.getAllResources()
                .stream()
                .map(ResourceDto::fromEntity)
                .collect(Collectors.toList());

        LOGGER.info("Resources: {}", resources.toString());
        return ResponseEntity.ok(resources);
    }

    // 📌 Ajouter une ressource (Admins & Professeurs uniquement)
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<Resource> addResource(@RequestBody Resource resource) {
        return ResponseEntity.ok(resourceService.addResource(resource));
    }

    // 📌 Supprimer une ressource (Seuls les Admins et les Professeurs ayant créé la ressource)
    @DeleteMapping("/{resourceId}")
    @PreAuthorize("hasRole('ADMIN') or @resourceService.isResourceOwner(#resourceId)")
    public ResponseEntity<Void> deleteResource(@PathVariable Long resourceId) {
        resourceService.deleteResource(resourceId);
        return ResponseEntity.noContent().build();
    }

    // 📌 Récupérer les ressources d'un cours (Professeurs et Étudiants inscrits au cours)
    @GetMapping("/by-course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'STUDENT', 'PARENT')")
    public ResponseEntity<List<Resource>> getResourcesByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(resourceService.getResourcesByCourse(courseId));
    }

/*
    // 📌 Récupérer toutes les ressources (Admins uniquement)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Resource>> getAllResources() {
        return ResponseEntity.ok(resourceService.getAllResources());
    }
*/
    @PostMapping("/upload")
    public ResponseEntity<ResourceDto> uploadResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("type") String type,
            @RequestParam("courseId") Long courseId,
            @RequestParam(value = "description", required = false) String description) {

        try {
            String filePath = storageService.storeFile(file); // Stockage du fichier
            Optional<Course> course = courseService.findById(courseId);

            Resource resource = new Resource();
            resource.setTitle(title);
            resource.setType(type);
            resource.setUrl(filePath);
            resource.setCourse(course.orElseThrow());
            resource.setDescription(description);

// Sauvegarde de l'entité directement sans conversion inutile
            Resource savedResource = resourceService.saveResource(resource);

// Conversion correcte de l'entité sauvegardée en DTO pour la réponse
            return ResponseEntity.ok(ResourceDto.fromEntity(savedResource));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

}
