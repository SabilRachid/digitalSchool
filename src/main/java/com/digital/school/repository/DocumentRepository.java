package com.digital.school.repository;

import com.digital.school.model.Document;
import com.digital.school.model.enumerated.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    // Récupère les documents liés à une entité (par exemple, Student ou Parent)
    List<Document> findByRelatedEntityIdAndRelatedEntityType(Long relatedEntityId, String relatedEntityType);

    // Récupère les documents d'un type spécifique
    List<Document> findByType(DocumentType type);

    // Récupère le premier document associé à une entité et d'un type donné
    Optional<Document> findFirstByRelatedEntityIdAndRelatedEntityTypeAndType(Long relatedEntityId,
                                                                             String relatedEntityType,
                                                                             DocumentType type);

    // Vérifie l'existence d'un document pour une entité donnée et un type spécifique
    boolean existsByRelatedEntityIdAndRelatedEntityTypeAndType(Long relatedEntityId,
                                                               String relatedEntityType,
                                                               DocumentType type);

    // Récupère les documents associés à un cours (pour les ressources de cours)
    @Query("SELECT d FROM Document d WHERE d.relatedEntityType = 'Course' AND d.relatedEntityId = :courseId")
    List<Document> findByCourseId(@Param("courseId") Long courseId);

    // Vérifie si un document est possédé par un professeur donné
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Document d WHERE d.id = :documentId AND d.owner.id = :professorId")
    boolean isOwner(@Param("documentId") Long documentId, @Param("professorId") Long professorId);

    // Recherche les documents dont la description contient une chaîne spécifique (par exemple, pour filtrer par catégorie)
    List<Document> findByDescriptionContaining(String keyword);

    List<Document> findByRelatedEntityIdAndRelatedEntityTypeAndType(Long id, String parent, DocumentType documentType);
}
