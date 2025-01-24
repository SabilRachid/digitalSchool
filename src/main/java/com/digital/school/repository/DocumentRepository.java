package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.digital.school.model.Document;
import com.digital.school.model.User;
import java.util.List;
import java.util.Map;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByStudent(User student);
    List<Document> findByParent(User parent);
    List<Document> findByCategory(String category);
    List<Document> findByType(String type);
    List<Document> findByUploadedBy(User uploadedBy);
    List<Document> findByValidatedBy(User validatedBy);
    
    @Query("SELECT d FROM Document d WHERE d.student = :student AND d.type = :type")
    List<Document> findByStudentAndType(@Param("student") User student, @Param("type") String type);
    
    @Query("SELECT d FROM Document d WHERE d.parent = :parent AND d.type = :type")
    List<Document> findByParentAndType(@Param("parent") User parent, @Param("type") String type);
    
    boolean existsByStudentAndType(User student, String type);
    boolean existsByParentAndType(User parent, String type);

    @Query("SELECT NEW map(" +
           "d.id as id, " +
           "d.name as name, " +
           "d.type as type, " +
           "d.category as category, " +
           "d.uploadedAt as uploadedAt, " +
           "d.validated as validated, " +
           "d.uploadedBy as uploadedBy, " +
           "d.student as student, " +
           "d.parent as parent) " +
           "FROM Document d")
    List<Map<String, Object>> findAllAsMap();

    @Query("SELECT COUNT(d) > 0 FROM Document d " +
           "WHERE d.student = :student " +
           "AND d.type = :type " +
           "AND d.validated = true")
    boolean hasValidatedDocument(@Param("student") User student, @Param("type") String type);

  
    @Query("SELECT d FROM Document d " +
           "WHERE d.validated = false " +
           "ORDER BY d.uploadedAt DESC")
    List<Document> findPendingValidation();

    @Query("SELECT COUNT(d) FROM Document d " +
           "WHERE d.validated = false")
    long countPendingValidation();

    @Query("SELECT NEW map(" +
           "d.type as type, " +
           "COUNT(d) as count) " +
           "FROM Document d " +
           "GROUP BY d.type")
    List<Map<String, Object>> getDocumentTypeStats();

    @Query("SELECT NEW map(" +
           "d.category as category, " +
           "COUNT(d) as count) " +
           "FROM Document d " +
           "GROUP BY d.category")
    List<Map<String, Object>> getDocumentCategoryStats();
}
