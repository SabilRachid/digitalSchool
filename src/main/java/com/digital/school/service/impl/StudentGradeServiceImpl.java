package com.digital.school.service.impl;

import com.digital.school.model.Student;
import com.digital.school.model.StudentExam;
import com.digital.school.model.StudentHomework;
import com.digital.school.model.StudentSubmission;
import com.digital.school.repository.StudentSubmissionRepository;
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

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StudentGradeServiceImpl implements StudentGradeService {

    @Autowired
    private StudentSubmissionRepository studentSubmissionRepository;

    @Autowired
    private PDFService pdfService;

    @Override
    public List<Map<String, Object>> findRecentGrades(Student student) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("gradedAt").descending());
        Page<StudentSubmission> pagedGrades = studentSubmissionRepository.findRecentGrades(student, pageable);
        return pagedGrades.getContent().stream()
                .map(this::convertSubmissionToMap)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> findGradesBySubject(Student student) {
        // On regroupe toutes les soumissions (devoirs et examens) par matière
        List<StudentSubmission> submissions = studentSubmissionRepository.findByStudentOrderByGradedAtDesc(student);
        List<StudentSubmission> filtered = submissions.stream()
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
            // Pour l'exemple, on réutilise la même moyenne pour la classe
            subjectData.put("classAverage", avg);
            return subjectData;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> findHomeworkGrades(Student student) {
        List<StudentSubmission> submissions = studentSubmissionRepository.findByStudentOrderByGradedAtDesc(student);
        List<StudentSubmission> homeworkSubmissions = submissions.stream()
                .filter(s -> s instanceof StudentHomework)
                .collect(Collectors.toList());
        return groupSubmissionsBySubject(homeworkSubmissions);
    }

    @Override
    public List<Map<String, Object>> findExamGrades(Student student) {
        List<StudentSubmission> submissions = studentSubmissionRepository.findByStudentOrderByGradedAtDesc(student);
        List<StudentSubmission> examSubmissions = submissions.stream()
                .filter(s -> s instanceof StudentExam)
                .collect(Collectors.toList());
        return groupSubmissionsBySubject(examSubmissions);
    }

    @Override
    public Map<String, Object> calculatePerformanceStats(Student student) {
        Map<String, Object> stats = new HashMap<>();
        List<StudentSubmission> allSubmissions = studentSubmissionRepository.findByStudentOrderByGradedAtDesc(student);

        double average = calculateAverage(allSubmissions);
        stats.put("average", average);

        int rank = studentSubmissionRepository.calculateStudentRank(
                student.getId(),
                student.getClasse().getId()
        );
        stats.put("rank", rank);

        Map<String, List<Double>> progression = getGradesProgression(student);
        stats.put("progression", progression);

        Map<String, Double> subjectAverages = getSubjectAverages(student);
        stats.put("subjectAverages", subjectAverages);

        // Ajout d'autres statistiques si nécessaire (ex: taux de réussite)
        Double successRate = studentSubmissionRepository.calculateSuccessRate(student);
        stats.put("successRate", successRate != null ? successRate : 0.0);

        // Moyenne générale (alternative)
        Double overallAverage = studentSubmissionRepository.calculateAverageGrade(student);
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
        List<StudentSubmission> submissions = studentSubmissionRepository.findByStudentOrderByGradedAtDesc(student);
        return submissions.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getEvaluation().getSubject().getName(),
                        Collectors.mapping(StudentSubmission::getValue, Collectors.toList())
                ));
    }

    @Override
    public int getStudentRank(Student student) {
        return studentSubmissionRepository.calculateStudentRank(
                student.getId(),
                student.getClasse().getId()
        );
    }

    @Override
    public Map<String, Double> getSubjectAverages(Student student) {
        List<StudentSubmission> submissions = studentSubmissionRepository.findByStudentOrderByGradedAtDesc(student);
        return submissions.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getEvaluation().getSubject().getName(),
                        Collectors.averagingDouble(StudentSubmission::getValue)
                ));
    }

    // Méthode utilitaire pour convertir une soumission en Map simple (DTO)
    private Map<String, Object> convertSubmissionToMap(StudentSubmission submission) {
        Map<String, Object> map = new HashMap<>();
        map.put("evaluationTitle", submission.getEvaluation().getTitle());
        map.put("dueDate", submission.getEvaluation().getDueDate()); // Vous pouvez formater la date si besoin
        map.put("value", submission.getValue());
        // Vous pouvez ajouter d'autres champs si nécessaire
        return map;
    }

    // Méthode utilitaire pour grouper une liste de soumissions par matière et créer un DTO
    private List<Map<String, Object>> groupSubmissionsBySubject(List<StudentSubmission> submissions) {
        Map<String, List<StudentSubmission>> grouped = submissions.stream()
                .collect(Collectors.groupingBy(s -> s.getEvaluation().getSubject().getName()));
        return grouped.entrySet().stream().map(entry -> {
            String subject = entry.getKey();
            List<StudentSubmission> subjectSubmissions = entry.getValue();
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
    private double calculateAverage(List<StudentSubmission> submissions) {
        if (submissions.isEmpty()) return 0.0;
        return submissions.stream()
                .mapToDouble(StudentSubmission::getValue)
                .average()
                .orElse(0.0);
    }

    // Méthode utilitaire pour calculer une moyenne de classe (implémentation basique)
    private double calculateClassAverage(List<StudentSubmission> submissions) {
        if (submissions.isEmpty()) return 0.0;
        return submissions.stream()
                .mapToDouble(StudentSubmission::getValue)
                .average()
                .orElse(0.0);
    }
}
