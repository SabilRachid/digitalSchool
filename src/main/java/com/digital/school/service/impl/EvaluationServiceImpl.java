package com.digital.school.service.impl;

import com.digital.school.model.Evaluation;
import com.digital.school.model.Professor;
import com.digital.school.model.StudentSubmission;
import com.digital.school.repository.EvaluationRepository;
import com.digital.school.repository.StudentSubmissionRepository;
import com.digital.school.service.EvaluationService;
import com.digital.school.service.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EvaluationServiceImpl implements EvaluationService {

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private StudentSubmissionRepository studentSubmissionRepository;

    @Autowired
    private PDFService pdfService;

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


    @Override
    public List<Map<String, Object>> findGroupedEvaluations(Long classeId, Long subjectId, String evaluationType, String startDate, String endDate) {
        List<Evaluation> evaluations = evaluationRepository.findAll();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate, dateFormatter) : null;
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate, dateFormatter) : null;

        return evaluations.stream()
                .filter(ev -> (classeId == null || ev.getClasse().getId().equals(classeId)) &&
                        (subjectId == null || ev.getSubject().getId().equals(subjectId)) &&
                        (evaluationType == null || ev.getClass().getSimpleName().toUpperCase().contains(evaluationType.toUpperCase())) &&
                        (start == null || !ev.getDueDate().isBefore(start)) &&
                        (end == null || !ev.getDueDate().isAfter(end)))
                .map(ev -> Map.<String, Object>of(
                        "id", ev.getId(),
                        "subjectName", ev.getSubject().getName(),
                        "evaluationType", ev.getClass().getSimpleName(),
                        "classeName", ev.getClasse().getName(),
                        "eventDate", ev.getDueDate().toString(),
                        "completed", ev.getStatus().name().equals("COMPLETED"),
                        // Si vous souhaitez afficher un nom d'élève associé, adaptez cette logique
                        "studentName", "N/A",
                        "classeId", ev.getClasse().getId()
                ))
                .collect(Collectors.toList());
    }



    @Override
    public List<Map<String, Object>> findGradesForEvaluation(Long evaluationId) {
        // Récupère toutes les soumissions associées à l'évaluation
        List<StudentSubmission> submissions = studentSubmissionRepository.findByEvaluationId(evaluationId);
        // Transformation des soumissions en maps
        return submissions.stream()
                .map(submission -> Map.<String, Object>of(
                        "studentId", submission.getStudent().getId(),
                        "studentName", submission.getStudent().getFirstName() + " " + submission.getStudent().getLastName(),
                        "value", submission.getValue(),
                        "comments", submission.getComments()
                ))
                .collect(Collectors.toList());

    }

    @Override
    public void saveGrades(Long evaluationId, List<Map<String, Object>> updates) {
        // Pour chaque mise à jour, on récupère la soumission correspondante et on la met à jour.
        for (Map<String, Object> update : updates) {
            Long studentId = Long.parseLong(update.get("studentId").toString());
            Double value = Double.parseDouble(update.get("value").toString());
            String comments = update.get("comments") != null ? update.get("comments").toString() : null;

            StudentSubmission submission = studentSubmissionRepository
                    .findByEvaluationIdAndStudentId(evaluationId, studentId)
                    .orElseThrow(() -> new RuntimeException("Soumission non trouvée pour l'étudiant " + studentId));

            submission.setValue(value);
            submission.setComments(comments);
            // Par exemple, vous pouvez mettre à jour le statut ici (si applicable)
            // submission.setStatus(StudentSubmissionStatus.COMPLETED);
            studentSubmissionRepository.save(submission);
        }
    }

    @Override
    public byte[] generateGradeReport(Long evaluationId, Long subjectId) {
        // Récupère l'évaluation correspondante
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new RuntimeException("Évaluation non trouvée"));
        // Récupère les notes associées à l'évaluation
        List<Map<String, Object>> grades = findGradesForEvaluation(evaluationId);
        // Prépare les données pour le rapport
        Map<String, Object> data = Map.of(
                "evaluation", evaluation,
                "grades", grades
        );
        // Génère le rapport PDF via le service PDFService
        return pdfService.generateReport("grade-report", data);
    }
}
