package com.digital.school.repository;

import com.digital.school.model.Student;
import com.digital.school.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.digital.school.model.StudentGrade;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StudentGradeRepository extends JpaRepository<StudentGrade, Long> {


    // Récupère les notes pour une classe et une matière donnés
    @Query("SELECT sg FROM StudentGrade sg WHERE sg.student.classe.id = :classeId AND sg.subject.id = :subjectId")
    List<StudentGrade> findByStudent_Classe_IdAndSubject_Id(@Param("classeId") Long classeId,
                                                            @Param("subjectId") Long subjectId);

    // Récupère toutes les notes associées à une évaluation donnée
    @Query("SELECT sg FROM StudentGrade sg WHERE sg.id = :evaluationId")
    List<StudentGrade> findByEvaluationId(@Param("evaluationId") Long evaluationId);

    // Récupère la note d'un étudiant pour une évaluation donnée
    @Query("SELECT sg FROM StudentGrade sg WHERE sg.id = :evaluationId AND sg.student.id = :studentId")
    Optional<StudentGrade> findByEvaluationIdAndStudentId(@Param("evaluationId") Long evaluationId,
                                                          @Param("studentId") Long studentId);

    // Calcule la moyenne des notes pour une classe et une matière donnés
    @Query("SELECT AVG(sg.value) FROM StudentGrade sg WHERE sg.student.classe.id = :classeId AND sg.subject.id = :subjectId")
    Double calculateAverageGradeByClasseAndSubject(@Param("classeId") Long classeId,
                                                   @Param("subjectId") Long subjectId);

    // Récupère la note la plus haute pour une classe et une matière donnés
    @Query("SELECT MAX(sg.value) FROM StudentGrade sg WHERE sg.student.classe.id = :classeId AND sg.subject.id = :subjectId")
    Double findHighestGradeByClasseAndSubject(@Param("classeId") Long classeId,
                                              @Param("subjectId") Long subjectId);

    // Récupère la note la plus basse pour une classe et une matière donnés
    @Query("SELECT MIN(sg.value) FROM StudentGrade sg WHERE sg.student.classe.id = :classeId AND sg.subject.id = :subjectId")
    Double findLowestGradeByClasseAndSubject(@Param("classeId") Long classeId,
                                             @Param("subjectId") Long subjectId);

    // Calcule le taux de réussite pour une classe et une matière donnés (note >= 10)
    @Query("SELECT (COUNT(CASE WHEN sg.value >= 10 THEN 1 END) * 100.0 / COUNT(sg)) " +
            "FROM StudentGrade sg WHERE sg.student.classe.id = :classeId AND sg.subject.id = :subjectId")
    Double calculatePassRateByClasseAndSubject(@Param("classeId") Long classeId,
                                               @Param("subjectId") Long subjectId);

    // Retourne la distribution des notes par intervalles pour une classe et une matière donnés.
    // Cette requête native utilise PostgreSQL et retourne un tableau d'entiers.
    @Query(value = "SELECT array_agg(cnt) FROM (" +
            "  SELECT count(*) as cnt FROM student_grades sg " +
            "  WHERE sg.value >= 0 AND sg.value < 5 AND sg.student_id IN (SELECT id FROM students WHERE class_id = :classeId) AND sg.subject_id = :subjectId " +
            "  UNION ALL " +
            "  SELECT count(*) FROM student_grades sg " +
            "  WHERE sg.value >= 5 AND sg.value < 8 AND sg.student_id IN (SELECT id FROM students WHERE class_id = :classeId) AND sg.subject_id = :subjectId " +
            "  UNION ALL " +
            "  SELECT count(*) FROM student_grades sg " +
            "  WHERE sg.value >= 8 AND sg.value < 10 AND sg.student_id IN (SELECT id FROM students WHERE class_id = :classeId) AND sg.subject_id = :subjectId " +
            "  UNION ALL " +
            "  SELECT count(*) FROM student_grades sg " +
            "  WHERE sg.value >= 10 AND sg.value < 12 AND sg.student_id IN (SELECT id FROM students WHERE class_id = :classeId) AND sg.subject_id = :subjectId " +
            "  UNION ALL " +
            "  SELECT count(*) FROM student_grades sg " +
            "  WHERE sg.value >= 12 AND sg.value < 15 AND sg.student_id IN (SELECT id FROM students WHERE class_id = :classeId) AND sg.subject_id = :subjectId " +
            "  UNION ALL " +
            "  SELECT count(*) FROM student_grades sg " +
            "  WHERE sg.value >= 15 AND sg.value < 18 AND sg.student_id IN (SELECT id FROM students WHERE class_id = :classeId) AND sg.subject_id = :subjectId " +
            "  UNION ALL " +
            "  SELECT count(*) FROM student_grades sg " +
            "  WHERE sg.value >= 18 AND sg.value <= 20 AND sg.student_id IN (SELECT id FROM students WHERE class_id = :classeId) AND sg.subject_id = :subjectId " +
            ") sub", nativeQuery = true)
    int[] getGradeDistributionByClasseAndSubject(@Param("classeId") Long classeId, @Param("subjectId") Long subjectId);



    // Récupère une note pour un étudiant (dans un contexte donné)
    @Query("SELECT sg FROM StudentGrade sg WHERE sg.student.id = :studentId")
    Optional<StudentGrade> findByStudentId(Long studentId);


    // grades page Students

    @Query("SELECT g FROM StudentGrade g WHERE g.student.classe.id = :classeId AND g.subject.id = :subjectId")
    List<StudentGrade> findByClasse_IdAndSubject_Id(Long classeId, Long subjectId);

    @Query("SELECT g FROM StudentGrade g WHERE g.student.classe.id = :classeId AND '1' = :period")
    List<StudentGrade> findByClasseAndPeriod(Long classeId, String period);

    @Query(value = "WITH StudentAverages AS (" +
            "  SELECT g.student_id, COALESCE(AVG(g.grade_value), 0.0) as avg_grade, " +
            "         DENSE_RANK() OVER (ORDER BY AVG(g.grade_value) DESC) as rank " +
            "  FROM student_grades g " +
            "  JOIN students s ON g.student_id = s.user_id " +
            "  WHERE s.class_id = :classeId " +
            "  GROUP BY g.student_id" +
            ") " +
            "SELECT COALESCE(rank, 0) FROM StudentAverages WHERE student_id = :studentId",
            nativeQuery = true)
    Integer calculateStudentRank(
            @Param("studentId") Long studentId,
            @Param("classeId") Long classeId
    );



    @Query("SELECT COALESCE(AVG(g.value), 0.0) FROM StudentGrade g WHERE g.student = :student")
    Double calculateAverageGrade(@Param("student") Student student);

    @Query("SELECT g FROM StudentGrade g WHERE g.student = :student ORDER BY g.date DESC")
    Page<StudentGrade> findRecentGrades(Student student, Pageable pageable);

    @Query("SELECT g FROM StudentGrade g WHERE g.student = :student AND g.value < 8")
    List<StudentGrade> findLowGrades(Student student);

    @Query("SELECT g FROM StudentGrade g WHERE g.student = :student ORDER BY g.date DESC")
    List<StudentGrade> findByStudentOrderByDateDesc(Student student);


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


    @Query("SELECT AVG(CASE WHEN g.value >= 10 THEN 1 ELSE 0 END) FROM StudentGrade g WHERE g.student = :student")
    Double calculateSuccessRate(Student student);
}