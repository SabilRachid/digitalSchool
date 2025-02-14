package com.digital.school.repository;

import com.digital.school.model.Classe;
import com.digital.school.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {



    @Query("SELECT COUNT(p) FROM Professor p")
    Long countProfessors();

    @Query("SELECT COUNT(p) FROM Professor p WHERE p.employmentType = 'CDI'")
    Long countFullTimeProfessors();

    @Query("SELECT COUNT(p) FROM Professor p WHERE p.employmentType = 'CDD' OR p.employmentType = 'STAGE'")
    Long countPartTimeProfessors();

    @Query("SELECT p FROM Professor p JOIN p.subjects s WHERE s.name = :subjectName")
    List<Professor> findBySubjectsName(String subjectName); // Récupérer les profs d'une matière

    @Query("SELECT p FROM Professor p WHERE :classe MEMBER OF p.classes")
    List<Professor> findByClasse(Classe classe);
}

