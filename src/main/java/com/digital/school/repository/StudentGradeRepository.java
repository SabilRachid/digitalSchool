package com.digital.school.repository;

import com.digital.school.model.Student;
import com.digital.school.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.digital.school.model.StudentGrade;

import java.awt.print.Pageable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StudentGradeRepository extends JpaRepository<StudentGrade, Long> {
    List<StudentGrade> findByClasse_IdAndSubject_Id(Long classeId, Long subjectId);
    
    List<StudentGrade> findByClasse_Id(Long classeId);

    List<StudentGrade> findByStudent_Id(Long studentId);

    List<StudentGrade> findByClasseAndPeriod(Long classeId, String period);

    @Query("SELECT g FROM StudentGrade g WHERE g.student.id = :studentId AND g.subject.id = :subjectId")
    List<StudentGrade> findByStudent_IdAndSubject_Id(Long studentId, Long subjectId);

    @Query("SELECT AVG(g.value) FROM StudentGrade g " +
           "WHERE g.student.id = :studentId AND g.subject.id = :subjectId")
    Double calculateStudentAverage(
        @Param("studentId") Long studentId,
        @Param("subjectId") Long subjectId
    );
    
    @Query("SELECT g.subject.name as subject, AVG(g.value) as average " +
           "FROM StudentGrade g WHERE g.student.classe.id = :classeId " +
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

    @Query("SELECT AVG(g.value) FROM StudentGrade g WHERE g.student = :student")
    Double calculateAverageGrade(Optional<Student> student);


    @Query("SELECT g FROM StudentGrade g WHERE g.student = :student ORDER BY g.date DESC")
    List<StudentGrade> findRecentGrades(Student student, Pageable pageable);

    @Query("SELECT g FROM StudentGrade g WHERE g.student = :student AND g.value < 8")
    List<StudentGrade> findLowGrades(Student child);

    @Query("SELECT g FROM StudentGrade g WHERE g.student = :student ORDER BY g.date DESC")
    List<StudentGrade> findByStudentOrderByDateDesc(Student child);


    @Query(value = "SELECT g.subject.name as subject, AVG(g.value) as average " +
            "FROM StudentGrade g WHERE g.student = :student " +
            "GROUP BY g.subject.name")
    Map<String, Double> calculateSubjectAverages(Student student);

    @Query(value = "SELECT g.subject.name as subject, AVG(g.value) as average " +
            "FROM StudentGrade g WHERE g.student = :student " +
            "GROUP BY g.subject.name")
    Map<String, List<Double>> calculateProgression(Student student);

    @Query("SELECT COUNT(g) FROM StudentGrade g WHERE g.student.classe.id = :classId")
    Long countStudentsInClass(Long classId);

    @Query("SELECT COUNT(g) FROM StudentGrade g WHERE g.subject.id = :subjectId AND g.title = :title AND g.student.classe.id = :classId")
    Long countGradesForClass(Long subjectId, String title, Long classId);

    @Query("SELECT AVG(g.value) FROM StudentGrade g WHERE g.subject.id = :subjectId AND g.title = :title AND g.student.classe.id = :classId")
    Double calculateClassAverage(Long subjectId, String title, Long classId);

    @Query("SELECT AVG(CASE WHEN g.value >= 10 THEN 1 ELSE 0 END) FROM StudentGrade g WHERE g.subject.id = :subjectId AND g.title = :title AND g.student.classe.id = :classId")
    Double calculateSuccessRate(Long subjectId, String title, Long classId);
}