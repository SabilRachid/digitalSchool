package com.digital.school.service.impl;

import com.digital.school.dto.HomeworkDTO;
import com.digital.school.model.*;
import com.digital.school.model.enumerated.EvaluationStatus;
import com.digital.school.model.enumerated.EventType;
import com.digital.school.model.enumerated.StudentSubmissionStatus;
import com.digital.school.repository.*;
import com.digital.school.service.HomeworkService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class HomeworkServiceImpl implements HomeworkService {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(HomeworkServiceImpl.class);

    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private StudentHomeworkRepository studentHomeworkRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClasseRepository classeRepository;

    @Autowired
    private SubjectRepository subjectRepository;


    @Override
    @Transactional
    public Homework createHomework(HomeworkDTO homeworkDTO, Long professorId) {
        // Conversion du DTO en entité

        LOGGER.debug("createHomework : "+homeworkDTO.toString());
        Homework homework = new Homework();
        homework.setType(EventType.HOMEWORK);
        homework.setTitle(homeworkDTO.getTitle());
        homework.setDescription(homeworkDTO.getDescription());
        homework.setStartTime(LocalDateTime.now());

        homework.setEndTime(homeworkDTO.getDueDate().atStartOfDay());
        homework.setDueDate(homeworkDTO.getDueDate());
        homework.setSubmittedCount(0);
        homework.setStatus(EvaluationStatus.SCHEDULED);
        homework.setGraded(false);
        LOGGER.debug("before classes and subjects");

        Subject subject = subjectRepository.findById(homeworkDTO.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Sujet non trouvé"));
                 Classe classe = classeRepository.findById(homeworkDTO.getClasseId())
                .orElseThrow(() -> new RuntimeException("Classe non trouvée"));
        homework.setSubject(subject);
        homework.setClasse(classe);
                Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé"));
       homework.setProfessor(professor);


        // Ajouter tous les élèves de la classe
        homework.setParticipants(new HashSet<>(studentRepository.getStudentsByClasseId(homeworkDTO.getClasseId())));

        // Ajouter le professeur associé à la matière pour cette classe
        professorRepository.findProfessorsByClasseId(homeworkDTO.getClasseId()).stream()
                .filter(prof -> prof.getSubjects().contains(homework.getSubject()))
                .forEach(homework.getParticipants()::add);

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
    public Optional<Homework> findById(Long id) {
        return homeworkRepository.findById(id);
    }


    @Override
    public List<Homework> findPendingGradingByProfessor(Professor professor) {
        return homeworkRepository.findPendingGradingByProfessor(professor, EvaluationStatus.PUBLISHED);
    }

    @Override
    public HomeworkDTO convertToDTO(Homework homework) {
        LOGGER.debug("convertToDTO");
        HomeworkDTO dto = new HomeworkDTO();
        dto.setId(homework.getId());
        dto.setTitle(homework.getTitle());
        dto.setDescription(homework.getDescription());
        dto.setDueDate(homework.getDueDate());
        dto.setGraded(homework.isGraded());
        // Ici, vous devez extraire le nom du sujet, du professeur et de la classe.
        // Par exemple :
        if (homework.getSubject() != null) {
            dto.setSubjectId(homework.getSubject().getId());
        }
        if (homework.getProfessor() != null) {
            dto.setProfessorName(homework.getProfessor().getLastName() + " " + homework.getProfessor().getFirstName());
        }
        if (homework.getClasse() != null) {
            dto.setClasseId(homework.getClasse().getId());
        }
        dto.setStatus(homework.getStatus());
        return dto;
    }
}
