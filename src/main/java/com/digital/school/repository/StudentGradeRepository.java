package com.digital.school.repository;

import com.digital.school.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.digital.school.model.StudentGrade;
import com.digital.school.model.User;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentGradeRepository extends JpaRepository<StudentGrade, Long> {
    List<StudentGrade> findByStudentOrderByDateDesc(User student);
    
    @Query("SELECT AVG(g.value) FROM StudentGrade g WHERE g.student = ?1")
    Double calculateAverageGrade(Optional<Student> student);
    
    @Query("SELECT g FROM StudentGrade g WHERE g.student = ?1 ORDER BY g.date DESC LIMIT 5")
    List<StudentGrade> findRecentGrades(User student);

    @Query("SELECT sg FROM StudentGrade sg WHERE sg.student.id = :studentId ORDER BY sg.date DESC")
    List<StudentGrade> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT sg FROM StudentGrade sg WHERE sg.subject.id = :subjectId ORDER BY sg.date DESC")
    List<StudentGrade> findBySubjectId(@Param("subjectId") Long subjectId);


    @Query("SELECT COUNT(DISTINCT s.id) FROM Student s WHERE s.classe.id = :classId")
    Long countStudentsInClass(Long classId);

    @Query("SELECT COUNT(g) FROM StudentGrade g WHERE g.subject.id = :subjectId AND g.title = :title AND g.student.classe.id = :classId")
    Long countGradesForClass(@Param("subjectId") Long subjectId, @Param("title") String title, @Param("classId") Long classId);

    @Query("SELECT AVG(g.value) FROM StudentGrade g WHERE g.subject.id = :subjectId AND g.title = :title AND g.student.classe.id = :classId")
    Double calculateClassAverage(@Param("subjectId") Long subjectId, @Param("title") String title, @Param("classId") Long classId);

}



