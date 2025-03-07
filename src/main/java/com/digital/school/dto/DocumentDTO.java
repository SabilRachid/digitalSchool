package com.digital.school.dto;

import com.digital.school.model.Course;
import com.digital.school.model.Document;
import com.digital.school.model.enumerated.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {



    private Long id;
    private String name;
    private String type; // Type de ressource : PDF, Vidéo, Lien, etc.
    private String url; // Chemin du fichier stocké ou lien externe
    private String description;
    private Long courseId;
    private String courseName; // Nom du cours associé

    // ✅ Convertir une entité `Resource` en DTO
    public static DocumentDTO fromEntity(Document document) {
        return new DocumentDTO(
                document.getId(),
                document.getName(),
                document.getType().name(),
                document.getFileUrl(),
                document.getDescription(),
                document.getCourse().getId(),
                document.getCourse().getName() // Suppose que Course a un `getTitle()`
        );
    }

    // ✅ Convertir un DTO en entité `Resource`
    // ✅ Conversion DTO → Entité avec association du `Course`
    public static Document toEntity(DocumentDTO dto, Course course) {
        Document document = new Document();
        document.setId(dto.getId());
        document.setName(dto.getName());
        document.setType(DocumentType.valueOf(dto.getType()));
        document.setFileUrl(dto.getUrl());
        document.setDescription(dto.getDescription());
        document.setCourse(course); // Associe le cours récupéré
        return document;
    }
}
