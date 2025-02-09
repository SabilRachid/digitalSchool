
package com.digital.school.service;

import com.digital.school.model.Exam;
import com.digital.school.model.ExamResult;
import com.digital.school.model.Professor;
import com.digital.school.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExamService {
    List<Exam> findByProfessor(Professor professor);
    Optional<Exam> findById(Long id);
    Exam save(Exam exam);
    void deleteById(Long id);
    Exam publish(Long id);
    ExamResult publishResults(Long id, ExamResult results);
    List<Exam> findUpcomingExams(Professor professor);
    Map<String, Object> getExamStatistics(Long examId);
}
