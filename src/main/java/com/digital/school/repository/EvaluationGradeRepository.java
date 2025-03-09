package com.digital.school.repository;

import com.digital.school.model.EvaluationGrade;
import com.digital.school.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EvaluationGradeRepository extends JpaRepository<EvaluationGrade, Long> {

    // Retourne les EvaluationGrade pour une classe et une matière données
    @Query("SELECT eg FROM EvaluationGrade eg " +
            "WHERE eg.student.classe.id = :classId " +
            "AND eg.evaluation.subject.id = :subjectId")
    List<EvaluationGrade> findByStudent_Classe_IdAndSubject_Id(@Param("classId") Long classId,
                                                               @Param("subjectId") Long subjectId);

    // Retourne toutes les notes pour une évaluation donnée
    @Query("SELECT eg FROM EvaluationGrade eg WHERE eg.evaluation.id = :evaluationId")
    List<EvaluationGrade> findByEvaluationId(@Param("evaluationId") Long evaluationId);

    // Retourne la note d'un étudiant pour une évaluation donnée
    @Query("SELECT eg FROM EvaluationGrade eg " +
            "WHERE eg.evaluation.id = :evaluationId " +
            "AND eg.student.id = :studentId")
    Optional<EvaluationGrade> findByEvaluationIdAndStudentId(@Param("evaluationId") Long evaluationId,
                                                             @Param("studentId") Long studentId);


    // Retourne la note la plus haute pour une classe et une matière données
    @Query("SELECT MAX(eg.grade) FROM EvaluationGrade eg " +
            "WHERE eg.student.classe.id = :classId " +
            "AND eg.evaluation.subject.id = :subjectId")
    Double findHighestGradeByClasseAndSubject(@Param("classId") Long classId,
                                              @Param("subjectId") Long subjectId);

    // Retourne la note la plus basse pour une classe et une matière données
    @Query("SELECT MIN(eg.grade) FROM EvaluationGrade eg " +
            "WHERE eg.student.classe.id = :classId " +
            "AND eg.evaluation.subject.id = :subjectId")
    Double findLowestGradeByClasseAndSubject(@Param("classId") Long classId,
                                             @Param("subjectId") Long subjectId);

    // Calcule le taux de réussite (note >= 10) pour une classe et une matière données
    @Query("SELECT (COUNT(CASE WHEN eg.grade >= 10 THEN 1 END) * 100.0 / COUNT(eg)) " +
            "FROM EvaluationGrade eg " +
            "WHERE eg.student.classe.id = :classId " +
            "AND eg.evaluation.subject.id = :subjectId")
    Double calculatePassRateByClasseAndSubject(@Param("classId") Long classId,
                                               @Param("subjectId") Long subjectId);

    // Retourne la distribution des notes sous forme d'un tableau d'entiers pour différents intervalles
    @Query(value = "SELECT array_agg(cnt) FROM (" +
            "  SELECT count(*) as cnt FROM evaluation_grades eg " +
            "  WHERE eg.grade >= 0 AND eg.grade < 5 " +
            "    AND eg.student_id IN (SELECT id FROM students WHERE class_id = :classId) " +
            "    AND eg.evaluation_id IN (SELECT id FROM evaluations WHERE subject_id = :subjectId) " +
            "  UNION ALL " +
            "  SELECT count(*) FROM evaluation_grades eg " +
            "  WHERE eg.grade >= 5 AND eg.grade < 8 " +
            "    AND eg.student_id IN (SELECT id FROM students WHERE class_id = :classId) " +
            "    AND eg.evaluation_id IN (SELECT id FROM evaluations WHERE subject_id = :subjectId) " +
            "  UNION ALL " +
            "  SELECT count(*) FROM evaluation_grades eg " +
            "  WHERE eg.grade >= 8 AND eg.grade < 10 " +
            "    AND eg.student_id IN (SELECT id FROM students WHERE class_id = :classId) " +
            "    AND eg.evaluation_id IN (SELECT id FROM evaluations WHERE subject_id = :subjectId) " +
            "  UNION ALL " +
            "  SELECT count(*) FROM evaluation_grades eg " +
            "  WHERE eg.grade >= 10 AND eg.grade < 12 " +
            "    AND eg.student_id IN (SELECT id FROM students WHERE class_id = :classId) " +
            "    AND eg.evaluation_id IN (SELECT id FROM evaluations WHERE subject_id = :subjectId) " +
            "  UNION ALL " +
            "  SELECT count(*) FROM evaluation_grades eg " +
            "  WHERE eg.grade >= 12 AND eg.grade < 15 " +
            "    AND eg.student_id IN (SELECT id FROM students WHERE class_id = :classId) " +
            "    AND eg.evaluation_id IN (SELECT id FROM evaluations WHERE subject_id = :subjectId) " +
            "  UNION ALL " +
            "  SELECT count(*) FROM evaluation_grades eg " +
            "  WHERE eg.grade >= 15 AND eg.grade < 18 " +
            "    AND eg.student_id IN (SELECT id FROM students WHERE class_id = :classId) " +
            "    AND eg.evaluation_id IN (SELECT id FROM evaluations WHERE subject_id = :subjectId) " +
            "  UNION ALL " +
            "  SELECT count(*) FROM evaluation_grades eg " +
            "  WHERE eg.grade >= 18 AND eg.grade <= 20 " +
            "    AND eg.student_id IN (SELECT id FROM students WHERE class_id = :classId) " +
            "    AND eg.evaluation_id IN (SELECT id FROM evaluations WHERE subject_id = :subjectId) " +
            ") sub", nativeQuery = true)
    int[] getGradeDistributionByClasseAndSubject(@Param("classId") Long classId,
                                                 @Param("subjectId") Long subjectId);

    // Récupère les EvaluationGrade d'un étudiant dont la note est inférieure à 8
    @Query("SELECT eg FROM EvaluationGrade eg WHERE eg.student = :student AND eg.grade < 8")
    List<EvaluationGrade> findLowGrades(@Param("student") Student student);

    // Récupère les EvaluationGrade d'un étudiant triées par date de saisie décroissante (gradedAt)
    @Query("SELECT eg FROM EvaluationGrade eg WHERE eg.student = :student ORDER BY eg.gradedAt DESC")
    List<EvaluationGrade> findByStudentOrderByGradedAtDesc(@Param("student") Student student);

    // Calcule la moyenne des notes d'un étudiant
    @Query("SELECT AVG(eg.grade) FROM EvaluationGrade eg WHERE eg.student = :student")
    Double calculateAverageGrade(@Param("student") Student student);

    @Query("SELECT AVG(CASE WHEN ss.grade >= 10 THEN 1 ELSE 0 END) " +
            "FROM EvaluationGrade ss " +
            "WHERE ss.evaluation.subject.id = :subjectId " +
            "AND ss.evaluation.title = :title " +
            "AND ss.student.classe.id = :classId")
    Double calculateSuccessRate(@Param("subjectId") Long subjectId,
                                @Param("title") String title,
                                @Param("classId") Long classId);


    @Query("SELECT COUNT(eg) FROM EvaluationGrade eg WHERE eg.student.classe.id = :classId")
    Long countStudentsInClass(@Param("classId") Long classId);

    @Query("SELECT COUNT(eg) FROM EvaluationGrade eg " +
            "WHERE eg.evaluation.subject.id = :subjectId " +
            "AND eg.evaluation.title = :title " +
            "AND eg.student.classe.id = :classId")
    Long countGradesForClass(@Param("subjectId") Long subjectId,
                             @Param("title") String title,
                             @Param("classId") Long classId);

    @Query("SELECT AVG(eg.grade) FROM EvaluationGrade eg " +
            "WHERE eg.evaluation.subject.id = :subjectId " +
            "AND eg.evaluation.title = :title " +
            "AND eg.student.classe.id = :classId")
    Double calculateClassAverage(@Param("subjectId") Long subjectId,
                                 @Param("title") String title,
                                 @Param("classId") Long classId);


    // Calcul du rang d'un étudiant dans sa classe basé sur la moyenne de ses notes (via EvaluationGrade)
    @Query(value = "WITH StudentAverages AS ( " +
            "  SELECT eg.student_id, AVG(eg.grade) AS avgGrade " +
            "  FROM evaluation_grades eg " +
            "  JOIN students s ON eg.student_id = s.user_id " +
            "  WHERE s.class_id = :classId " +
            "  GROUP BY eg.student_id " +
            ") " +
            "SELECT DENSE_RANK() OVER (ORDER BY avgGrade DESC) AS rank " +
            "FROM StudentAverages " +
            "WHERE student_id = :studentId", nativeQuery = true)
    Integer calculateStudentRank(@Param("studentId") Long studentId,
                                 @Param("classId") Long classId);


    @Query("SELECT (COUNT(CASE WHEN eg.grade >= 10 THEN 1 END) * 100.0 / NULLIF(COUNT(eg), 0)) " +
            "FROM EvaluationGrade eg " +
            "WHERE eg.student = :student")
    Double calculateSuccessRate(@Param("student") Student student);

    @Query("SELECT eg FROM EvaluationGrade eg WHERE eg.student = :student ORDER BY eg.gradedAt DESC")
    Page<EvaluationGrade> findRecentGrades(@Param("student") Student student, Pageable pageable);

    @Query("SELECT eg FROM EvaluationGrade eg " +
            "WHERE eg.student = :student " +
            "AND eg.submitted = false " +
            "AND eg.evaluation.dueDate < CURRENT_DATE")
    List<EvaluationGrade> findLateHomework(@Param("student") Student student);

    @Query(value = "SELECT TO_CHAR(eg.graded_at, 'Mon') AS month, eg.grade " +
            "FROM evaluation_grades eg " +
            "WHERE eg.student_id = :studentId " +
            "ORDER BY eg.graded_at", nativeQuery = true)
    List<Object[]> findGradesByMonth(@Param("studentId") Long studentId);

    @Query("SELECT eg FROM EvaluationGrade eg " +
            "WHERE eg.student.classe.id = :classId " +
            "AND ((:period = 'past' AND " +
            "      ((TYPE(eg.evaluation) = Homework AND eg.evaluation.dueDate < CURRENT_DATE) " +
            "       OR (TYPE(eg.evaluation) = Exam AND eg.evaluation.startTime < CURRENT_DATE))) " +
            "     OR (:period = 'upcoming' AND " +
            "      ((TYPE(eg.evaluation) = Homework AND eg.evaluation.dueDate >= CURRENT_DATE) " +
            "       OR (TYPE(eg.evaluation) = Exam AND eg.evaluation.startTime >= CURRENT_DATE))))")
    List<EvaluationGrade> findByClasseAndPeriod(@Param("classId") Long classId,
                                                @Param("period") String period);

    @Query("SELECT eg.evaluation.subject.name, AVG(eg.grade) " +
            "FROM EvaluationGrade eg " +
            "WHERE eg.student = :student " +
            "GROUP BY eg.evaluation.subject.name")
    List<Object[]> findSubjectAveragesByStudent(@Param("student") Student student);


}



