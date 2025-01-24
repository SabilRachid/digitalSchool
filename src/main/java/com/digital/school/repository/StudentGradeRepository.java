package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.digital.school.model.StudentGrade;
import com.digital.school.model.User;
import java.util.List;

public interface StudentGradeRepository extends JpaRepository<StudentGrade, Long> {
    List<StudentGrade> findByStudentOrderByDateDesc(User student);
    
    @Query("SELECT AVG(g.value) FROM StudentGrade g WHERE g.student = ?1")
    Double calculateAverageGrade(User student);
    
    @Query("SELECT g FROM StudentGrade g WHERE g.student = ?1 ORDER BY g.date DESC LIMIT 5")
    List<StudentGrade> findRecentGrades(User student);
}

