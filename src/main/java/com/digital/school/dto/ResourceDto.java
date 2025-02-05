package com.digital.school.dto;

import com.digital.school.model.Course;
import com.digital.school.model.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDto {



    private Long id;
    private String title;
    private String type; // Type de ressource : PDF, Vidéo, Lien, etc.
    private String url; // Chemin du fichier stocké ou lien externe
    private String description;
    private Long courseId;
    private String courseName; // Nom du cours associé

    // ✅ Convertir une entité `Resource` en DTO
    public static ResourceDto fromEntity(Resource resource) {
        return new ResourceDto(
                resource.getId(),
                resource.getTitle(),
                resource.getType(),
                resource.getUrl(),
                resource.getDescription(),
                resource.getCourse().getId(),
                resource.getCourse().getName() // Suppose que Course a un `getTitle()`
        );
    }

    // ✅ Convertir un DTO en entité `Resource`
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
}
