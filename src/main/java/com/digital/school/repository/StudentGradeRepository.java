package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.digital.school.model.StudentGrade;
import com.digital.school.model.User;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentGradeRepository extends JpaRepository<StudentGrade, Long> {
    List<StudentGrade> findByStudentOrderByDateDesc(User student);
    
    @Query("SELECT AVG(g.value) FROM StudentGrade g WHERE g.student = ?1")
    Double calculateAverageGrade(Student student);
    
    @Query("SELECT g FROM StudentGrade g WHERE g.student = ?1 ORDER BY g.date DESC LIMIT 5")
    List<StudentGrade> findRecentGrades(User student);

    @Query("SELECT sg FROM StudentGrade sg WHERE sg.student.id = :studentId ORDER BY sg.date DESC")
    List<StudentGrade> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT sg FROM StudentGrade sg WHERE sg.subject.id = :subjectId ORDER BY sg.date DESC")
    List<StudentGrade> findBySubjectId(@Param("subjectId") Long subjectId);

}



