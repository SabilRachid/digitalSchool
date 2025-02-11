package com.digital.school.service;



import com.digital.school.model.Student;
import com.digital.school.model.StudentGrade;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface StudentGradeService {
    List<StudentGrade> findRecentGrades(Student student);
    List<Map<String, Object>> findGradesBySubject(Student student);
    Map<String, Object> calculatePerformanceStats(Student student);
    byte[] generateReport(Student student);
    @NotNull Map<String, List<Float>> getGradesProgression(Student student);
    int getStudentRank(Student student);
    Map<String, Double> getSubjectAverages(Student student);
}
