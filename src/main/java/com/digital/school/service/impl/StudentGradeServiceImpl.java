package com.digital.school.service.impl;

import com.digital.school.model.EvaluationGrade;
import com.digital.school.model.Student;
import com.digital.school.model.Homework;
import com.digital.school.model.Exam;
import com.digital.school.repository.EvaluationGradeRepository;
import com.digital.school.repository.EvaluationRepository;
import com.digital.school.repository.EventRepository;
import com.digital.school.repository.ParentStudentRepository;
import com.digital.school.repository.StudentAttendanceRepository;
import com.digital.school.service.PDFService;
import com.digital.school.service.StudentGradeService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StudentGradeServiceImpl implements StudentGradeService {

    @Autowired
    private EvaluationGradeRepository evaluationGradeRepository;

    @Autowired
    private PDFService pdfService;

    @Autowired
    private ParentStudentRepository parentStudentRepository;

    @Autowired
    private StudentAttendanceRepository studentAttendanceRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Override
    public List<Map<String, Object>> findRecentGrades(Student student) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("gradedAt").descending());
        Page<EvaluationGrade> pagedGrades = evaluationGradeRepository.findRecentGrades(student, pageable);
        return pagedGrades.getContent().stream()
                .map(this::convertSubmissionToMap)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> findGradesBySubject(Student student) {
        // On récupère toutes les soumissions triées par date décroissante
        List<EvaluationGrade> submissions = evaluationGradeRepository.findByStudentOrderByGradedAtDesc(student);
        List<EvaluationGrade> filtered = submissions.stream()
                .filter(s -> s.getEvaluation() != null && s.getEvaluation().getSubject() != null)
                .collect(Collectors.toList());

        Map<String, List<Map<String, Object>>> grouped = filtered.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getEvaluation().getSubject().getName(),
                        Collectors.mapping(this::convertSubmissionToMap, Collectors.toList())
                ));

        return grouped.entrySet().stream().map(entry -> {
            Map<String, Object> subjectData = new HashMap<>();
            subjectData.put("subject", entry.getKey());
            subjectData.put("grades", entry.getValue());
            double avg = entry.getValue().stream()
                    .mapToDouble(m -> ((Number) m.get("value")).doubleValue())
                    .average().orElse(0.0);
            subjectData.put("average", avg);
            // Ici, on réutilise la moyenne calculée comme moyenne de classe (à adapter)
            subjectData.put("classAverage", avg);
            return subjectData;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> findHomeworkGrades(Student student) {
        List<EvaluationGrade> submissions = evaluationGradeRepository.findByStudentOrderByGradedAtDesc(student);
        // Filtrer pour ne conserver que les évaluations de type Homework
        List<EvaluationGrade> homeworkSubmissions = submissions.stream()
                .filter(s -> s.getEvaluation() instanceof Homework)
                .collect(Collectors.toList());
        return groupSubmissionsBySubject(homeworkSubmissions);
    }

    @Override
    public List<Map<String, Object>> findExamGrades(Student student) {
        List<EvaluationGrade> submissions = evaluationGradeRepository.findByStudentOrderByGradedAtDesc(student);
        // Filtrer pour ne conserver que les évaluations de type Exam
        List<EvaluationGrade> examSubmissions = submissions.stream()
                .filter(s -> s.getEvaluation() instanceof Exam)
                .collect(Collectors.toList());
        return groupSubmissionsBySubject(examSubmissions);
    }

    @Override
    public Map<String, Object> calculatePerformanceStats(Student student) {
        Map<String, Object> stats = new HashMap<>();
        List<EvaluationGrade> allSubmissions = evaluationGradeRepository.findByStudentOrderByGradedAtDesc(student);

        double average = calculateAverage(allSubmissions);
        stats.put("average", average);

        Integer rank = evaluationGradeRepository.calculateStudentRank(student.getId(), student.getClasse().getId());
        stats.put("rank", rank != null ? rank : 0);

        Map<String, List<Double>> progression = getGradesProgression(student);
        stats.put("progression", progression);

        Map<String, Double> subjectAverages = getSubjectAverages(student);
        stats.put("subjectAverages", subjectAverages);

        Double successRate = evaluationGradeRepository.calculateSuccessRate(student);
        stats.put("successRate", successRate != null ? successRate : 0.0);

        Double overallAverage = evaluationGradeRepository.calculateAverageGrade(student);
        stats.put("averageGrade", overallAverage != null ? overallAverage : 0.0);

        return stats;
    }

    @Override
    public byte[] generateReport(Student student) {
        Map<String, Object> data = new HashMap<>();
        data.put("student", student);
        Map<String, Object> gradesBySubject = new HashMap<>();
        gradesBySubject.put("homeworkGrades", findHomeworkGrades(student));
        gradesBySubject.put("examGrades", findExamGrades(student));
        data.put("grades", gradesBySubject);
        data.put("stats", calculatePerformanceStats(student));
        return pdfService.generateReport("student-report", data);
    }

    @Override
    public @NotNull Map<String, List<Double>> getGradesProgression(Student student) {
        List<EvaluationGrade> submissions = evaluationGradeRepository.findByStudentOrderByGradedAtDesc(student);
        return submissions.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getEvaluation().getSubject().getName(),
                        Collectors.mapping(EvaluationGrade::getGrade, Collectors.toList())
                ));
    }

    @Override
    public int getStudentRank(Student student) {
        Integer rank = evaluationGradeRepository.calculateStudentRank(student.getId(), student.getClasse().getId());
        return rank != null ? rank : 0;
    }

    @Override
    public Map<String, Double> getSubjectAverages(Student student) {
        List<EvaluationGrade> submissions = evaluationGradeRepository.findByStudentOrderByGradedAtDesc(student);
        return submissions.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getEvaluation().getSubject().getName(),
                        Collectors.averagingDouble(EvaluationGrade::getGrade)
                ));
    }

    // Méthode utilitaire pour convertir une soumission en Map simple (DTO)
    private Map<String, Object> convertSubmissionToMap(EvaluationGrade submission) {
        Map<String, Object> map = new HashMap<>();
        map.put("evaluationTitle", submission.getEvaluation().getTitle());
        map.put("dueDate", submission.getEvaluation().getDueDate());
        map.put("value", submission.getGrade());
        return map;
    }

    // Méthode utilitaire pour grouper une liste de soumissions par matière et créer un DTO
    private List<Map<String, Object>> groupSubmissionsBySubject(List<EvaluationGrade> submissions) {
        Map<String, List<EvaluationGrade>> grouped = submissions.stream()
                .collect(Collectors.groupingBy(s -> s.getEvaluation().getSubject().getName()));
        return grouped.entrySet().stream().map(entry -> {
            String subject = entry.getKey();
            List<EvaluationGrade> subjectSubmissions = entry.getValue();
            double average = calculateAverage(subjectSubmissions);
            double classAverage = calculateClassAverage(subjectSubmissions);
            Map<String, Object> subjectData = new HashMap<>();
            subjectData.put("subject", subject);
            subjectData.put("grades", subjectSubmissions.stream()
                    .map(this::convertSubmissionToMap)
                    .collect(Collectors.toList()));
            subjectData.put("average", average);
            subjectData.put("classAverage", classAverage);
            return subjectData;
        }).collect(Collectors.toList());
    }

    // Méthode utilitaire pour calculer la moyenne d'une liste de soumissions
    private double calculateAverage(List<EvaluationGrade> submissions) {
        if (submissions.isEmpty()) return 0.0;
        return submissions.stream()
                .mapToDouble(EvaluationGrade::getGrade)
                .average()
                .orElse(0.0);
    }

    // Méthode utilitaire pour calculer une moyenne de classe (implémentation basique)
    private double calculateClassAverage(List<EvaluationGrade> submissions) {
        if (submissions.isEmpty()) return 0.0;
        return submissions.stream()
                .mapToDouble(EvaluationGrade::getGrade)
                .average()
                .orElse(0.0);
    }
}
