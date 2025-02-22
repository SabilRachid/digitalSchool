package com.digital.school.repository;

import com.digital.school.model.Evaluation;
import com.digital.school.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByProfessor(Professor professor);
}
