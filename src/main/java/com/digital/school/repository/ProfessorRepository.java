package com.digital.school.repository;

import com.digital.school.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    List<Professor> findBySubjects_Name(String subjectName); // Récupérer les profs d'une matière
}

