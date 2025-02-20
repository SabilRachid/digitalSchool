package com.digital.school.service.impl;

import com.digital.school.model.Student;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.StudentGrade;
import com.digital.school.model.User;
import com.digital.school.repository.StudentGradeRepository;
import com.digital.school.service.StudentGradeService;
import com.digital.school.service.PDFService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StudentGradeServiceImpl implements StudentGradeService {

    @Autowired
    private StudentGradeRepository gradeRepository;
    
    @Autowired
    private PDFService pdfService;

    @Override
    public List<StudentGrade> findRecentGrades(Student student) {
        //add pageable parameter to the method findRecentGrades(student);
        Pageable pageable = PageRequest.of(0, 5, Sort.by("date").descending());

        Page<StudentGrade> pagedGrades =gradeRepository.findRecentGrades(student, pageable);

        return pagedGrades.getContent();
    }

    @Override
    public List<Map<String, Object>> findGradesBySubject(Student student) {
        List<StudentGrade> grades = gradeRepository.findByStudentOrderByDateDesc(student);

        // Filtrer les notes dont la matière est null pour éviter des NullPointerExceptions
        List<StudentGrade> filteredGrades = grades.stream()
                .filter(grade -> grade.getSubject() != null)
                .collect(Collectors.toList());

        return filteredGrades.stream()
                .collect(Collectors.groupingBy(
                        grade -> grade.getSubject().getName(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                subjectGrades -> {
                                    Map<String, Object> subjectData = new HashMap<>();
                                    subjectData.put("grades", subjectGrades);
                                    subjectData.put("average", calculateAverage(subjectGrades));
                                    subjectData.put("classAverage", calculateClassAverage(subjectGrades));
                                    return subjectData;
                                }
                        )
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("subject", entry.getKey());
                    map.putAll(entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> calculatePerformanceStats(Student student) {
        Map<String, Object> stats = new HashMap<>();
        
        List<StudentGrade> allGrades = gradeRepository.findByStudentOrderByDateDesc(student);
        
        // Moyenne générale
        double average = calculateAverage(allGrades);
        stats.put("average", average);
        
        // Rang dans la classe
        int rank = gradeRepository.calculateStudentRank(
            student.getId(), 
            student.getClasse().getId()
        );
        stats.put("rank", rank);
        
        // Progression
        Map<String, List<Double>> progression = getGradesProgression(student);
        stats.put("progression", progression);
        
        // Moyennes par matière
        Map<String, Double> subjectAverages = getSubjectAverages(student);
        stats.put("subjectAverages", subjectAverages);
        
        return stats;
    }

    @Override
    public byte[] generateReport(Student student) {
        Map<String, Object> data = new HashMap<>();
        data.put("student", student);
        data.put("grades", findGradesBySubject(student));
        data.put("stats", calculatePerformanceStats(student));
        
        return pdfService.generateReport("student-report", data);
    }

    @Override
    public @NotNull Map<String, List<Double>> getGradesProgression(Student student) {
        List<StudentGrade> grades = gradeRepository.findByStudentOrderByDateDesc(student);
        
        return grades.stream()
            .collect(Collectors.groupingBy(
                grade -> grade.getSubject().getName(),
                Collectors.mapping(
                    StudentGrade::getValue,
                    Collectors.toList()
                )
            ));
    }

    @Override
    public int getStudentRank(Student student) {
        return gradeRepository.calculateStudentRank(
            student.getId(), 
            student.getClasse().getId()
        );
    }

    @Override
    public Map<String, Double> getSubjectAverages(Student student) {
        List<StudentGrade> grades = gradeRepository.findByStudentOrderByDateDesc(student);
        
        return grades.stream()
            .collect(Collectors.groupingBy(
                grade -> grade.getSubject().getName(),
                Collectors.averagingDouble(StudentGrade::getValue)
            ));
    }

    private double calculateAverage(List<StudentGrade> grades) {
        if (grades.isEmpty()) return 0.0;
        return grades.stream()
                .mapToDouble(StudentGrade::getValue)
                .average()
                .orElse(0.0);
    }

    private double calculateClassAverage(List<StudentGrade> grades) {
        if (grades.isEmpty()) return 0.0;
        
        return grades.stream()
            .mapToDouble(StudentGrade::getClassAverage)
            .average()
            .orElse(0.0);
    }
}