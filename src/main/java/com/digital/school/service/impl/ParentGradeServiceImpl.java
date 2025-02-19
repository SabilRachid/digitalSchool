package com.digital.school.service.impl;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.*;
import com.digital.school.repository.*;
import com.digital.school.service.ParentGradeService;
import com.digital.school.service.PDFService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ParentGradeServiceImpl implements ParentGradeService {

    @Autowired
    private ParentStudentRepository parentStudentRepository;
    
    @Autowired
    private StudentGradeRepository gradeRepository;
    
    @Autowired
    private PDFService pdfService;

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
        
        // Statistiques globales
        stats.put("averageGrade", calculateAverageGrade(child));
        stats.put("rank", calculateRank(child));
        stats.put("totalStudents", child.getClasse().getStudents().size());
        
        // Moyennes par matière
        stats.put("subjectAverages", calculateSubjectAverages(child));
        
        // Progression
        stats.put("progression", calculateProgression(child));
        
        // Distribution des notes
        stats.put("gradeDistribution", calculateGradeDistribution(child));
        
        return stats;
    }

    private double calculateAverageGrade(Student student) {
        return gradeRepository.calculateAverageGrade(student);
    }

    private int calculateRank(Student student) {
        return gradeRepository.calculateStudentRank(
            student.getId(), 
            student.getClasse().getId()
        );
    }

    private List<Map<String, Object>> getSubjectGrades(Student student) {
        return gradeRepository.findByStudentOrderByDateDesc(student).stream()
            .collect(Collectors.groupingBy(
                grade -> grade.getSubject().getName(),
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
                map.putAll((Map<String, Object>)entry.getValue());
                return map;
            })
            .collect(Collectors.toList());
    }

    private Map<String, Double> calculateSubjectAverages(Student student) {
        return gradeRepository.findByStudentOrderByDateDesc(student).stream()
            .collect(Collectors.groupingBy(
                grade -> grade.getSubject().getName(),
                Collectors.averagingDouble(StudentGrade::getValue)
            ));
    }

    private @NotNull Map<String, List<Double>> calculateProgression(Student student) {
        return gradeRepository.findByStudentOrderByDateDesc(student).stream()
            .collect(Collectors.groupingBy(
                grade -> grade.getSubject().getName(),
                Collectors.mapping(
                    StudentGrade::getValue,
                    Collectors.toList()
                )
            ));
    }

    private Map<String, Long> calculateGradeDistribution(Student student) {
        return gradeRepository.findByStudentOrderByDateDesc(student).stream()
            .collect(Collectors.groupingBy(
                this::getGradeRange,
                Collectors.counting()
            ));
    }

    private String getGradeRange(StudentGrade grade) {
        Double value = grade.getValue();
        if (value < 5) return "0-5";
        if (value < 8) return "5-8";
        if (value < 10) return "8-10";
        if (value < 12) return "10-12";
        if (value < 15) return "12-15";
        if (value < 18) return "15-18";
        return "18-20";
    }

    private double calculateAverage(List<StudentGrade> grades) {
        return grades.stream()
            .mapToDouble(StudentGrade::getValue)
            .average()
            .orElse(0.0);
    }

    private double calculateClassAverage(List<StudentGrade> grades) {
        return grades.stream()
            .mapToDouble(StudentGrade::getClassAverage)
            .average()
            .orElse(0.0);
    }
}