package com.digital.school.repository;

import com.digital.school.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByCourseId(Long courseId); // 📌 Récupérer les ressources par cours

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END FROM Resource r WHERE r.id = :resourceId AND r.course.professor.id = :professorId")
    boolean existsByIdAndCourse_Professor_Id(Long resourceId, Long professorId);
}
