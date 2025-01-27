package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.digital.school.model.Classe;
import java.util.List;

public interface ClasseRepository extends JpaRepository<Classe, Long> {
    boolean existsByNameAndSchoolYear(String name, String schoolYear);
    
    @Query("SELECT c FROM Classe c WHERE c.level.id = ?1")
    List<Classe> findByLevelId(Long levelId);
    
    @Query("SELECT COUNT(s) FROM Classe c JOIN c.students s WHERE c.id = ?1")
    long countStudents(Long classeId);

    @Query("SELECT c FROM Classe c JOIN FETCH c.subjects WHERE c.id = ?1")
    Classe findByIdWithSubjects(Long id);
}