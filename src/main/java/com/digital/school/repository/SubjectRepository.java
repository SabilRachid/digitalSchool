package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.digital.school.model.Subject;
import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    boolean existsByCode(String code);
    boolean existsByName(String name);
    Optional<Subject> findByCode(String code);
    
    @Query("SELECT s FROM Subject s JOIN s.classes c WHERE c.id = ?1")
    List<Subject> findByClasseId(Long classeId);
    
    @Query("SELECT s FROM Subject s JOIN s.levels l WHERE l.id = ?1")
    List<Subject> findByLevelId(Long levelId);
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.subject.id = ?1")
    long countCourses(Long subjectId);

    @Query("SELECT s.name AS subjectName, COUNT(p.id) AS professorCount " +
            "FROM Subject s JOIN s.professors p JOIN p.roles r " +
            "WHERE r.name = 'ROLE_PROFESSOR' " +
            "GROUP BY s.name")
    List<Object[]> findProfessorCountBySubject();
}