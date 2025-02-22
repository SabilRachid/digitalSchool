package com.digital.school.service;

import com.digital.school.model.Evaluation;
import com.digital.school.model.Professor;
import java.util.List;
import java.util.Optional;

public interface EvaluationService {

    Optional<Evaluation> findById(Long id);

    List<Evaluation> findAllByProfessor(Professor professor);

    Evaluation createEvaluation(Evaluation evaluation, Professor professor);

    Evaluation updateEvaluation(Long id, Evaluation evaluation, Professor professor);

    void deleteEvaluation(Long id, Professor professor);
}
