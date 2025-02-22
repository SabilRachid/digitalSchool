package com.digital.school.service.impl;

import com.digital.school.model.Evaluation;
import com.digital.school.model.Professor;
import com.digital.school.repository.EvaluationRepository;
import com.digital.school.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EvaluationServiceImpl implements EvaluationService {

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Override
    public Optional<Evaluation> findById(Long id) {
        return evaluationRepository.findById(id);
    }

    @Override
    public List<Evaluation> findAllByProfessor(Professor professor) {
        // On suppose que EvaluationRepository possède une méthode findByProfessor.
        return evaluationRepository.findByProfessor(professor);
    }

    @Override
    public Evaluation createEvaluation(Evaluation evaluation, Professor professor) {
        evaluation.setProfessor(professor);
        return evaluationRepository.save(evaluation);
    }

    @Override
    public Evaluation updateEvaluation(Long id, Evaluation evaluation, Professor professor) {
        Evaluation existing = evaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Évaluation non trouvée"));
        if (!existing.getProfessor().equals(professor)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier cette évaluation");
        }
        existing.setTitle(evaluation.getTitle());
        existing.setDueDate(evaluation.getDueDate());
        existing.setSubject(evaluation.getSubject());
        existing.setClasse(evaluation.getClasse());
        existing.setStatus(evaluation.getStatus());
        return evaluationRepository.save(existing);
    }

    @Override
    public void deleteEvaluation(Long id, Professor professor) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Évaluation non trouvée"));
        if (!evaluation.getProfessor().equals(professor)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer cette évaluation");
        }
        evaluationRepository.delete(evaluation);
    }
}
