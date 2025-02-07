package com.digital.school.service;



import com.digital.school.model.StudentGrade;
import com.digital.school.model.User;
import java.util.List;
import java.util.Map;

public interface StudentGradeService {
    List<StudentGrade> findRecentGrades(User student);
    List<Map<String, Object>> findGradesBySubject(User student);
    Map<String, Object> calculatePerformanceStats(User student);
    byte[] generateReport(User student);
    Map<String, List<Double>> getGradesProgression(User student);
    int getStudentRank(User student);
    Map<String, Double> getSubjectAverages(User student);
}
