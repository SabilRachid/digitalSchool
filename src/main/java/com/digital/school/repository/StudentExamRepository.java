package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.digital.school.model.StudentExam;
import com.digital.school.model.User;
import com.digital.school.model.Exam;
import java.util.List;
import java.util.Optional;

public interface StudentExamRepository extends JpaRepository<StudentExam, Long> {
    List<StudentExam> findByStudent(User student);
    List<StudentExam> findByExam(Exam exam);
    Optional<StudentExam> findByStudentAndExam(User student, Exam exam);
    
    @Query("SELECT AVG(se.score) FROM StudentExam se WHERE se.exam = :exam")
    Double calculateAverageScore(Exam exam);
    
    @Query("SELECT MAX(se.score) FROM StudentExam se WHERE se.exam = :exam")
    Double findHighestScore(Exam exam);
    
    @Query("SELECT MIN(se.score) FROM StudentExam se WHERE se.exam = :exam")
    Double findLowestScore(Exam exam);
    
    @Query("SELECT se.student FROM StudentExam se WHERE se.exam = :exam AND se.score = (SELECT MAX(se2.score) FROM StudentExam se2 WHERE se2.exam = :exam)")
    User findTopScorer(Exam exam);
}