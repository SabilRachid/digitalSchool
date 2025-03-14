package com.digital.school.service.impl;

import com.digital.school.dto.HomeworkDTO;
import com.digital.school.model.*;
import com.digital.school.model.enumerated.EvaluationStatus;
import com.digital.school.model.enumerated.EventType;
import com.digital.school.repository.*;
import com.digital.school.service.HomeworkService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class HomeworkServiceImpl implements HomeworkService {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(HomeworkServiceImpl.class);

    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private EvaluationGradeRepository evaluationGradeRepository;

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
    public void publishHomework(Long evaluationId) {
        Homework evaluation = homeworkRepository.findById(evaluationId)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));

        // Mettre à jour le status (ex: SCHEDULED -> UPCOMING)
        evaluation.setStatus(com.digital.school.model.enumerated.EvaluationStatus.UPCOMING);

        // Récupération de la liste des étudiants de la classe associée à l'évaluation
        Set<Student> students = evaluation.getClasse().getStudents();

        // Pour chaque étudiant, s'il n'existe pas déjà une note enregistrée, on crée une entrée EvaluationGrade vide
        for (Student student : students) {
            LOGGER.debug("set Grade Evaluation for student="+student.toString());
            Optional<EvaluationGrade> existingGrade = evaluationGradeRepository
                    .findByEvaluationIdAndStudentId(evaluationId, student.getId());
            if (existingGrade.isEmpty()) {
                LOGGER.debug("create new EvaluationGrade");
                EvaluationGrade grade = new EvaluationGrade();
                grade.setEvaluation(evaluation);
                grade.setStudent(student);
                grade.setGrade(null); // ou 0.0 si vous préférez une valeur numérique par défaut
                grade.setRemark("");
                grade.setGradedAt(null);
                evaluationGradeRepository.save(grade);
            }
        }

        homeworkRepository.save(evaluation);


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
        Optional<EvaluationGrade> submissionOpt = evaluationGradeRepository.findById(submissionId);
        if (submissionOpt.isEmpty()) {
            throw new RuntimeException("Soumission non trouvée");
        }
        EvaluationGrade submission = submissionOpt.get();
        if (!submission.getEvaluation().getProfessor().getId().equals(professorId)) {
            throw new RuntimeException("Accès non autorisé");
        }
        submission.setGrade(gradeValue);
        submission.setRemark(comment);
        submission.setSubmitted(true);
        submission.setGradedAt(LocalDateTime.now());
        evaluationGradeRepository.save(submission);
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
        return homeworkRepository.findPendingGradingByProfessor(professor, EvaluationStatus.UPCOMING);
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

    @Override
    public List<Map<String, Object>> getChildrenHomework(Parent parent) {
        // Supposons que le parent dispose d'une méthode getChildren() renvoyant ses enfants.
        List<Student> children = (List<Student>) parent.getChildren();
        return children.stream()
                .map(child -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("childId", child.getId());
                    // Ici, on récupère la liste des devoirs pour l'enfant via le repository
                    map.put("homeworks", homeworkRepository.findByStudent(child));
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getDetailedChildHomework(Long childId) {
        Map<String, Object> details = new HashMap<>();
        // Récupérer la liste des devoirs pour l'enfant identifié par childId
       studentRepository.findById(childId)
                .ifPresentOrElse(student -> {
                    List<Homework> homeworks = homeworkRepository.findByStudent(student);
                    details.put("homeworks", homeworks);
                }, () -> {
                    throw new RuntimeException("Enfant non trouvé");
                });
        // Vous pouvez ajouter ici d'autres informations détaillées (notes, feedback, etc.)
        return details;
    }

    @Override
    public Map<String, Object> getChildHomeworkStats(Long childId) {
        Map<String, Object> stats = new HashMap<>();
        // Exemple : nombre total de devoirs pour l'enfant
        long total = homeworkRepository.countByStudentId(childId);
        stats.put("totalHomeworks", total);
        // Vous pouvez ajouter d'autres statistiques (moyenne des notes, taux d'achèvement, etc.)
        return stats;
    }

    @Override
    public void sendHomeworkReminder(Long homeworkId) {
        // Implémentez ici la logique d'envoi d'un rappel (par e-mail ou SMS)
        // Par exemple, récupérer le devoir, puis envoyer une notification.
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));
        // Exemple : si le devoir est en attente et que sa date d'échéance est dépassée
        if (homework.getDueDate().isBefore(LocalDate.now())) {
            // Appelez ici votre service de notification (email, SMS, etc.)
            // Exemple : notificationService.sendHomeworkReminder(homework);
        }
    }

    @Override
    public List<Map<String, Object>> getHomeworkGrades(Long homeworkId) {
        // Récupérer le devoir (Homework) par son identifiant
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé pour l'ID : " + homeworkId));

        // Supposons que Homework étend Evaluation, utilisez l'ID de l'évaluation pour récupérer les notes
        List<EvaluationGrade> grades = evaluationGradeRepository.findByEvaluationId(homework.getId());

        // Conversion de chaque EvaluationGrade en Map<String, Object>
        return grades.stream()
                .map(grade -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("studentId", grade.getStudent().getId());
                    map.put("firstName", grade.getStudent().getFirstName());
                    map.put("lastName", grade.getStudent().getLastName());
                    map.put("grade", grade.getGrade());
                    map.put("comment", grade.getRemark());
                    return map;
                })
                .collect(Collectors.toList());
    }

}
