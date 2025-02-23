package com.digital.school.service;

import com.digital.school.model.Student;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface StudentGradeService {
    List<Map<String, Object>> findRecentGrades(Student student);
    List<Map<String, Object>> findGradesBySubject(Student student);
    List<Map<String, Object>> findHomeworkGrades(Student student);
    List<Map<String, Object>> findExamGrades(Student student);
    Map<String, Object> calculatePerformanceStats(Student student);
    byte[] generateReport(Student student);
    @NotNull Map<String, List<Double>> getGradesProgression(Student student);
    int getStudentRank(Student student);
    Map<String, Double> getSubjectAverages(Student student);
}
