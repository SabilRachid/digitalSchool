package com.digital.school.service.impl;

import com.digital.school.model.Homework;
import com.digital.school.model.Professor;
import com.digital.school.model.enumerated.EvaluationStatus;
import com.digital.school.model.enumerated.StudentSubmissionStatus;
import com.digital.school.repository.HomeworkRepository;
import com.digital.school.repository.ParentHomeworkRepository;
import com.digital.school.repository.StudentHomeworkRepository;
import com.digital.school.service.HomeworkService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class HomeworkServiceImpl implements HomeworkService {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(HomeworkServiceImpl.class);

    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private StudentHomeworkRepository studentHomeworkRepository;


    @Override
    public Homework createHomework(Homework homework, Long professorId) {
        homework.setProfessor(new Professor(professorId));
        homework.setStatus(EvaluationStatus.SCHEDULED); // Par défaut, le devoir est planifié
        return homeworkRepository.save(homework);
    }

    @Override
    public void publishHomework(Long homeworkId) {
        Homework hw = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));
        hw.setStatus(EvaluationStatus.PUBLISHED);
        homeworkRepository.save(hw);
    }

    @Override
    public void endHomework(Long homeworkId) {
        Homework hw = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));
        hw.setStatus(EvaluationStatus.COMPLETED);
        homeworkRepository.save(hw);
    }

    @Override
    public void enterGrade(Long submissionId, Double gradeValue, String comment, Long professorId) {
        var submissionOpt = studentHomeworkRepository.findById(submissionId);
        if (submissionOpt.isEmpty()) {
            throw new RuntimeException("Soumission non trouvée");
        }
        var submission = submissionOpt.get();
        if (!submission.getEvaluation().getProfessor().getId().equals(professorId)) {
            throw new RuntimeException("Accès non autorisé");
        }
        submission.setValue(gradeValue);
        submission.setComments(comment);
        submission.setStatus(StudentSubmissionStatus.COMPLETED);
        studentHomeworkRepository.save(submission);
    }

    @Override
    public List<Homework> findHomeworksByProfessor(Long professorId) {
        return homeworkRepository.findByProfessorId(professorId);
    }

    @Override
    public List<Homework> findHomeworksByProfessor(Long professorId, String month, Long classe, Long subject) {
        List<Homework> homeworks = homeworkRepository.findByProfessorId(professorId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return homeworks.stream().filter(hw -> {
            boolean matches = true;
            if (month != null && !month.isEmpty()) {
                String hwMonth = hw.getDueDate().format(formatter);
                matches = matches && hwMonth.equals(month);
            }
            if (classe != null) {
                matches = matches && hw.getClasse().getId().equals(classe);
            }
            if (subject != null) {
                matches = matches && hw.getSubject().getId().equals(subject);
            }
            return matches;
        }).collect(Collectors.toList());
    }

    @Override
    public byte[] generateHomeworkReport(Long homeworkId) {
        // Implémentez ici la génération du rapport PDF, par exemple via iText ou JasperReports.
        throw new UnsupportedOperationException("Génération du rapport non implémentée");
    }

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
