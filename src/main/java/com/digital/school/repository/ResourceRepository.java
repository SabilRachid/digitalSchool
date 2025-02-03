package com.digital.school.repository;

import com.digital.school.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    // Trouver les ressources par matière
    List<Resource> findBySubjectId(Long subjectId);

    // Trouver les ressources par professeur
    List<Resource> findByProfessorId(Long professorId);
}
