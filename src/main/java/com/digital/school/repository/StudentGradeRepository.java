package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.digital.school.model.StudentGrade;
import java.util.List;
import java.util.Map;

public interface StudentGradeRepository extends JpaRepository<StudentGrade, Long> {
    List<StudentGrade> findByClasse_IdAndSubject_Id(Long classeId, Long subjectId);
    
    List<StudentGrade> findByClasse_Id(Long classeId);
    
    @Query("SELECT AVG(g.value) FROM StudentGrade g " +
           "WHERE g.student.id = :studentId AND g.subject.id = :subjectId")
    Double calculateStudentAverage(
        @Param("studentId") Long studentId,
        @Param("subjectId") Long subjectId
    );
    
    @Query("SELECT g.subject.name as subject, AVG(g.value) as average " +
           "FROM StudentGrade g WHERE g.classe.id = :classeId " +
           "GROUP BY g.subject.name")
    Map<String, Double> calculateClassAverages(@Param("classeId") Long classeId);
    
    @Query(value = "WITH StudentAverages AS (" +
                   "  SELECT student_id, AVG(value) as avg_grade, " +
                   "         DENSE_RANK() OVER (ORDER BY AVG(value) DESC) as rank " +
                   "  FROM student_grades " +
                   "  WHERE classe_id = :classeId " +
                   "  GROUP BY student_id" +
                   ") " +
                   "SELECT rank FROM StudentAverages WHERE student_id = :studentId",
           nativeQuery = true)
    int calculateStudentRank(
        @Param("studentId") Long studentId,
        @Param("classeId") Long classeId
    );
}