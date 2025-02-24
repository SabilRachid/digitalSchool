package com.digital.school.service.impl;

import com.digital.school.model.Exam;
import com.digital.school.model.Professor;
import com.digital.school.model.Student;
import com.digital.school.model.StudentSubmission;
import com.digital.school.repository.ExamRepository;
import com.digital.school.repository.StudentSubmissionRepository;
import com.digital.school.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private StudentSubmissionRepository studentSubmissionRepository;

    @Override
    public Exam createExam(Exam exam, Long professorId) {
        // Liez l'examen au professeur selon votre logique métier
        // Par exemple, si l'examen doit être lié au professeur
        exam.setProfessor(new Professor(professorId)); // Supposons que Professor a un constructeur par ID
        return examRepository.save(exam);
    }

    @Override
    public void publishExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Examen non trouvé"));
        // Mettre à jour le status (ex: SCHEDULED -> PUBLISHED)
        exam.setStatus(com.digital.school.model.enumerated.EvaluationStatus.PUBLISHED);
        examRepository.save(exam);
    }

    @Override
    public void endExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Examen non trouvé"));
        // Mettre à jour le status pour indiquer qu'il est terminé
        exam.setStatus(com.digital.school.model.enumerated.EvaluationStatus.COMPLETED);
        examRepository.save(exam);
    }

    @Override
    public Map<String, Object> getExamResults(Long examId) {
        // Implémentez la logique pour calculer les résultats de l'examen
        // Par exemple, calculer la moyenne, la note max, etc.
        // Retournez un Map avec les informations nécessaires
        throw new UnsupportedOperationException("Méthode non implémentée");
    }

    @Override
    public byte[] generateExamReport(Long examId) {
        // Implémentez la génération du rapport PDF pour l'examen
        throw new UnsupportedOperationException("Méthode non implémentée");
    }

    @Override
    public StudentSubmission enterGrade(Long submissionId, Double gradeValue, String comment, Long professorId) {
        // Récupérer la soumission d'examen par son identifiant
        StudentSubmission submission = studentSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Soumission non trouvée"));
        // Vérifiez que le professeur a le droit de noter cette soumission
        // (par exemple, en vérifiant que submission.getEvaluation().getProfessor().getId() == professorId)
        submission.setValue(gradeValue);
        submission.setComments(comment);
        // Vous pouvez mettre à jour le status, par exemple passer de IN_PROGRESS à COMPLETED si nécessaire.
        submission.setStatus(com.digital.school.model.enumerated.StudentSubmissionStatus.COMPLETED);
        return studentSubmissionRepository.save(submission);
    }

    @Override
    public List<Exam> findUpcomingExams(Student student) {
        return examRepository.findUpcomingExamsByStudent(student.getId(), LocalDate.now());
    }

    @Override
    public List<Exam> findExamsByProfessor(Long professorId) {
        return examRepository.findByProfessorId(professorId);
    }

    @Override
    public List<Exam> findExamsByProfessor(Long professorId, String month, Long classe, Long subject) {
        // Récupère d'abord tous les examens du professeur
        List<Exam> exams = examRepository.findByProfessorId(professorId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return exams.stream().filter(exam -> {
            boolean matches = true;
            if (month != null && !month.isEmpty()) {
                // On formate examDate en "yyyy-MM"
                String examMonth = exam.getStartTime().format(formatter);
                matches = matches && examMonth.equals(month);
            }
            if (classe != null) {
                matches = matches && exam.getClasse().getId().equals(classe);
            }
            if (subject != null) {
                matches = matches && exam.getSubject().getId().equals(subject);
            }
            return matches;
        }).collect(Collectors.toList());
    }

}


