package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.StudentGrade;
import com.digital.school.repository.StudentGradeRepository;
import com.digital.school.service.GradeService;
import com.digital.school.service.PDFService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GradeServiceImpl implements GradeService {

    @Autowired
    private StudentGradeRepository gradeRepository;
    
    @Autowired
    private PDFService pdfService;

    @Override
    public List<StudentGrade> findByClasseAndSubject(Long classeId, Long subjectId) {
        return gradeRepository.findByClasse_IdAndSubject_Id(classeId, subjectId);
    }

    @Override
    @Transactional
    public List<StudentGrade> saveBulk(List<StudentGrade> grades) {
        // Calculer la moyenne de la classe
        double classAverage = grades.stream()
            .mapToDouble(StudentGrade::getValue)
            .average()
            .orElse(0.0);
            
        grades.forEach(grade -> grade.setClassAverage(classAverage));
        
        return gradeRepository.saveAll(grades);
    }

    @Override
    public byte[] generateClassReport(Long classeId) {
        Map<String, Object> data = new HashMap<>();
        
        // Récupérer toutes les notes de la classe
        List<StudentGrade> grades = gradeRepository.findByClasse_Id(classeId);
        
        // Organiser les données par étudiant et par matière
        Map<Long, Map<String, List<Float>>> gradesByStudent = grades.stream()
            .collect(Collectors.groupingBy(
                grade -> grade.getStudent().getId(),
                Collectors.groupingBy(
                    grade -> grade.getSubject().getName(),
                    Collectors.mapping(StudentGrade::getValue, Collectors.toList())
                )
            ));
            
        // Calculer les moyennes
        Map<Long, Map<String, Double>> averagesByStudent = new HashMap<>();
        gradesByStudent.forEach((studentId, subjectGrades) -> {
            Map<String, Double> averages = new HashMap<>();
            subjectGrades.forEach((subject, values) -> {
                double average = values.stream()
                    .mapToDouble(Float::doubleValue)
                    .average()
                    .orElse(0.0);
                averages.put(subject, average);
            });
            averagesByStudent.put(studentId, averages);
        });
        
        data.put("grades", gradesByStudent);
        data.put("averages", averagesByStudent);
        
        return pdfService.generateReport("class-report", data);
    }

    @Override
    public Map<String, Object> calculateClassStatistics(Long classeId) {
        Map<String, Object> stats = new HashMap<>();
        
        List<StudentGrade> grades = gradeRepository.findByClasse_Id(classeId);
        
        // Moyenne générale
        double classAverage = grades.stream()
            .mapToDouble(StudentGrade::getValue)
            .average()
            .orElse(0.0);
            
        // Distribution des notes
        Map<String, Long> distribution = grades.stream()
            .collect(Collectors.groupingBy(this::getGradeRange, Collectors.counting()));
            
        // Taux de réussite
        long passingGrades = grades.stream()
            .filter(grade -> grade.getValue() >= 10)
            .count();
        double passRate = (double) passingGrades / grades.size() * 100;
        
        stats.put("average", classAverage);
        stats.put("distribution", distribution);
        stats.put("passRate", passRate);
        
        return stats;
    }

    @Override
    public Double calculateStudentAverage(Long studentId, Long subjectId) {
        return gradeRepository.calculateStudentAverage(studentId, subjectId);
    }

    @Override
    public Map<String, Double> calculateClassAverages(Long classeId) {
        return gradeRepository.calculateClassAverages(classeId);
    }

    @Override
    public int calculateStudentRank(Long studentId, Long classeId) {
        return gradeRepository.calculateStudentRank(studentId, classeId);
    }

    private String getGradeRange(StudentGrade grade) {
        float value = grade.getValue();
        if (value < 5) return "0-5";
        if (value < 8) return "5-8";
        if (value < 10) return "8-10";
        if (value < 12) return "10-12";
        if (value < 15) return "12-15";
        if (value < 18) return "15-18";
        return "18-20";
    }
}
