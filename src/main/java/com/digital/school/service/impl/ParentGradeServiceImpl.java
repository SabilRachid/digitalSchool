package com.digital.school.service.impl;

import com.digital.school.model.Parent;
import com.digital.school.model.ParentStudent;
import com.digital.school.model.Student;
import com.digital.school.model.EvaluationGrade;
import com.digital.school.repository.EvaluationGradeRepository;
import com.digital.school.repository.ParentStudentRepository;
import com.digital.school.repository.ParentHomeworkRepository;
import com.digital.school.repository.EvaluationRepository;
import com.digital.school.repository.EventRepository;
import com.digital.school.repository.StudentAttendanceRepository;
import com.digital.school.service.EmailService;
import com.digital.school.service.ParentGradeService;
import com.digital.school.service.PDFService;
import com.digital.school.service.SMSService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ParentGradeServiceImpl implements ParentGradeService {

    @Autowired
    private ParentStudentRepository parentStudentRepository;

    // L'utilisation de EvaluationGradeRepository pour récupérer les notes
    @Autowired
    private EvaluationGradeRepository evaluationGradeRepository;

    // Références aux autres repositories
    @Autowired
    private ParentHomeworkRepository homeworkRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private StudentAttendanceRepository studentAttendanceRepository;

    @Autowired
    private PDFService pdfService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SMSService smsService;

    @Override
    public List<Map<String, Object>> getChildrenGrades(Parent parent) {
        return parentStudentRepository.findByParent(parent).stream()
                .map(association -> {
                    Student child = association.getStudent();
                    Map<String, Object> childGrades = new HashMap<>();
                    childGrades.put("childId", child.getId());
                    childGrades.put("childName", child.getFirstName() + " " + child.getLastName());
                    childGrades.put("class", child.getClasse().getName());
                    childGrades.put("averageGrade", calculateAverageGrade(child));
                    childGrades.put("rank", calculateRank(child));
                    childGrades.put("subjectGrades", getSubjectGrades(child));
                    return childGrades;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getDetailedChildGrades(Long childId) {
        Student child = parentStudentRepository.findByStudentId(childId)
                .map(ParentStudent::getStudent)
                .orElseThrow(() -> new RuntimeException("Enfant non trouvé"));
        Map<String, Object> details = new HashMap<>();
        details.put("student", child);
        details.put("grades", getSubjectGrades(child));
        details.put("stats", getChildGradeStats(childId));
        details.put("progression", calculateProgression(child));
        return details;
    }

    @Override
    public byte[] generateChildReport(Long childId) {
        Student child = parentStudentRepository.findByStudentId(childId)
                .map(ParentStudent::getStudent)
                .orElseThrow(() -> new RuntimeException("Enfant non trouvé"));
        Map<String, Object> data = new HashMap<>();
        data.put("student", child);
        data.put("grades", getSubjectGrades(child));
        data.put("stats", getChildGradeStats(childId));
        data.put("class", child.getClasse());
        return pdfService.generateReport("student-report", data);
    }

    @Override
    public Map<String, Object> getChildGradeStats(Long childId) {
        Student child = parentStudentRepository.findByStudentId(childId)
                .map(ParentStudent::getStudent)
                .orElseThrow(() -> new RuntimeException("Enfant non trouvé"));

        Map<String, Object> stats = new HashMap<>();
        stats.put("averageGrade", calculateAverageGrade(child));
        stats.put("rank", calculateRank(child));
        stats.put("totalStudents", child.getClasse().getStudents().size());
        stats.put("subjectAverages", calculateSubjectAverages(child));
        stats.put("progression", calculateProgression(child));
        stats.put("gradeDistribution", calculateGradeDistribution(child));
        return stats;
    }

    @Override
    public List<Map<String, Object>> getChildGrades(Long childId) {
        Student child = parentStudentRepository.findByStudentId(childId)
                .map(ParentStudent::getStudent)
                .orElseThrow(() -> new RuntimeException("Enfant non trouvé"));

        return evaluationGradeRepository.findByStudentOrderByGradedAtDesc(child).stream()
                .map(grade -> {
                    Map<String, Object> gradeMap = new HashMap<>();
                    gradeMap.put("subject", grade.getEvaluation().getSubject().getName());
                    gradeMap.put("value", grade.getGrade());
                    gradeMap.put("title", grade.getEvaluation().getTitle());
                    gradeMap.put("date", grade.getGradedAt());
                    return gradeMap;
                })
                .collect(Collectors.toList());
    }



    // Méthodes utilitaires

    private double calculateAverageGrade(Student student) {
        Double avg = evaluationGradeRepository.calculateAverageGrade(student);
        return avg != null ? avg : 0.0;
    }

    private int calculateRank(Student student) {
        Integer rank = evaluationGradeRepository.calculateStudentRank(student.getId(), student.getClasse().getId());
        return rank != null ? rank : 0;
    }

    private List<Map<String, Object>> getSubjectGrades(Student student) {
        return evaluationGradeRepository.findByStudentOrderByGradedAtDesc(student).stream()
                .collect(Collectors.groupingBy(
                        grade -> grade.getEvaluation().getSubject().getName(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                grades -> {
                                    Map<String, Object> subjectData = new HashMap<>();
                                    subjectData.put("grades", grades);
                                    subjectData.put("average", calculateAverage(grades));
                                    subjectData.put("classAverage", calculateClassAverage(grades));
                                    return subjectData;
                                }
                        )
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("subject", entry.getKey());
                    map.putAll((Map<String, Object>) entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    private Map<String, Double> calculateSubjectAverages(Student student) {
        return evaluationGradeRepository.findByStudentOrderByGradedAtDesc(student).stream()
                .collect(Collectors.groupingBy(
                        grade -> grade.getEvaluation().getSubject().getName(),
                        Collectors.averagingDouble(EvaluationGrade::getGrade)
                ));
    }

    private @NotNull Map<String, List<Double>> calculateProgression(Student student) {
        return evaluationGradeRepository.findByStudentOrderByGradedAtDesc(student).stream()
                .collect(Collectors.groupingBy(
                        grade -> grade.getEvaluation().getSubject().getName(),
                        Collectors.mapping(EvaluationGrade::getGrade, Collectors.toList())
                ));
    }

    private Map<String, Long> calculateGradeDistribution(Student student) {
        return evaluationGradeRepository.findByStudentOrderByGradedAtDesc(student).stream()
                .collect(Collectors.groupingBy(
                        this::getGradeRange,
                        Collectors.counting()
                ));
    }

    private String getGradeRange(EvaluationGrade grade) {
        Double value = grade.getGrade();
        if (value < 5) return "0-5";
        if (value < 8) return "5-8";
        if (value < 10) return "8-10";
        if (value < 12) return "10-12";
        if (value < 15) return "12-15";
        if (value < 18) return "15-18";
        return "18-20";
    }

    private double calculateAverage(List<EvaluationGrade> grades) {
        return grades.stream()
                .mapToDouble(EvaluationGrade::getGrade)
                .average()
                .orElse(0.0);
    }

    private double calculateClassAverage(List<EvaluationGrade> grades) {
        return grades.stream()
                .mapToDouble(EvaluationGrade::getGrade)
                .average()
                .orElse(0.0);
    }
}
