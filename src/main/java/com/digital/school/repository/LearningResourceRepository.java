package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.digital.school.model.LearningResource;
import com.digital.school.model.Subject;
import java.util.List;

public interface LearningResourceRepository extends JpaRepository<LearningResource, Long> {
    List<LearningResource> findBySubjectOrderByUploadedAtDesc(Subject subject);
    
    @Query("SELECT r FROM LearningResource r WHERE r.subject = ?1 ORDER BY r.uploadedAt DESC LIMIT 5")
    List<LearningResource> findRecentResources(Subject subject);


    long countBySubject(Subject subject);
}
