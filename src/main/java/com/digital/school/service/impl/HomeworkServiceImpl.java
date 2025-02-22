package com.digital.school.service.impl;

import com.digital.school.model.Homework;
import com.digital.school.model.Professor;
import com.digital.school.model.enumerated.EvaluationStatus;
import com.digital.school.repository.ParentHomeworkRepository;
import com.digital.school.service.HomeworkService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class HomeworkServiceImpl implements HomeworkService {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(HomeworkServiceImpl.class);

    @Autowired
    private ParentHomeworkRepository homeworkRepository;

    @Override
    public List<Map<String, Object>> findAllAsMap(Long classId, Integer year, Integer month) {
        LOGGER.debug("findAllAsMap by classId=" + classId + " ,year=" + year + " ,month=" + month);
        return homeworkRepository.findAllAsMap(classId, year, month);
    }

    @Override
    public Homework findByIdAndProfessor(Long id, Professor professor) {
        return homeworkRepository.findByIdAndProfessor(id, professor)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé ou accès refusé"));
    }

    @Override
    public Homework createHomework(Homework homework, Professor professor) {
        homework.setProfessor(professor);
        return homeworkRepository.save(homework);
    }

    @Override
    public Homework updateHomework(Long id, Homework homework, Professor professor) {
        Homework existing = findByIdAndProfessor(id, professor);
        existing.setTitle(homework.getTitle());
        existing.setDescription(homework.getDescription());
        existing.setDueDate(homework.getDueDate());
        // Vous pouvez ajouter d'autres mises à jour si nécessaire
        return homeworkRepository.save(existing);
    }

    @Override
    public Optional<Homework> findById(Long id) {
        return homeworkRepository.findById(id);
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteHomework(Long homeworkId, Professor professor) {
        Homework homework = findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));
        if (!homework.getProfessor().equals(professor)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer ce devoir");
        }
        homeworkRepository.delete(homework);
        return ResponseEntity.ok("Devoir supprimé avec succès.");
    }

    @Override
    public List<Homework> findHomeworksByProfessor(Professor professor) {
        return homeworkRepository.findByProfessor(professor);
    }

    @Override
    public List<Homework> findPendingGradingByProfessor(Professor professor) {
        return homeworkRepository.findPendingGradingByProfessor(professor, EvaluationStatus.PUBLISHED);
    }
}
