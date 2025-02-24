package com.digital.school.repository;

import com.digital.school.model.Student;
import com.digital.school.model.StudentSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StudentSubmissionRepository extends JpaRepository<StudentSubmission, Long> {

    @Query(value = "WITH StudentAverages AS ( " +
            "  SELECT ss.student_id, " +
            "         COALESCE(AVG(ss.grade_value), 0.0) AS avg_grade, " +
            "         DENSE_RANK() OVER (ORDER BY AVG(ss.grade_value) DESC) AS rank " +
            "  FROM student_submissions ss " +
            "  JOIN students s ON ss.student_id = s.user_id " +
            "  WHERE s.class_id = :classId " +
            "  GROUP BY ss.student_id " +
            ") " +
            "SELECT COALESCE(rank, 0) " +
            "FROM StudentAverages " +
            "WHERE student_id = :studentId",
            nativeQuery = true)
    Integer calculateStudentRank(@Param("studentId") Long studentId,
                                 @Param("classId") Long classId);

    // Récupère les soumissions pour une classe et une matière donnés.
    @Query("SELECT ss FROM StudentSubmission ss " +
            "WHERE ss.student.classe.id = :classId " +
            "AND ss.evaluation.subject.id = :subjectId")
    List<StudentSubmission> findByStudent_Classe_IdAndSubject_Id(@Param("classId") Long classId,
                                                                 @Param("subjectId") Long subjectId);

    // Récupère toutes les soumissions associées à une évaluation donnée.
    @Query("SELECT ss FROM StudentSubmission ss WHERE ss.evaluation.id = :evaluationId")
    List<StudentSubmission> findByEvaluationId(@Param("evaluationId") Long evaluationId);

    // Récupère la soumission d'un étudiant pour une évaluation donnée.
    @Query("SELECT ss FROM StudentSubmission ss " +
            "WHERE ss.evaluation.id = :evaluationId " +
            "AND ss.student.id = :studentId")
    Optional<StudentSubmission> findByEvaluationIdAndStudentId(@Param("evaluationId") Long evaluationId,
                                                               @Param("studentId") Long studentId);

    // Calcule la moyenne des notes pour une classe et une matière donnés.
    @Query("SELECT AVG(ss.value) FROM StudentSubmission ss " +
            "WHERE ss.student.classe.id = :classId " +
            "AND ss.evaluation.subject.id = :subjectId")
    Double calculateAverageGradeByClasseAndSubject(@Param("classId") Long classId,
                                                   @Param("subjectId") Long subjectId);

    // Récupère la note la plus haute pour une classe et une matière donnés.
    @Query("SELECT MAX(ss.value) FROM StudentSubmission ss " +
            "WHERE ss.student.classe.id = :classId " +
            "AND ss.evaluation.subject.id = :subjectId")
    Double findHighestGradeByClasseAndSubject(@Param("classId") Long classId,
                                              @Param("subjectId") Long subjectId);

    // Récupère la note la plus basse pour une classe et une matière donnés.
    @Query("SELECT MIN(ss.value) FROM StudentSubmission ss " +
            "WHERE ss.student.classe.id = :classId " +
            "AND ss.evaluation.subject.id = :subjectId")
    Double findLowestGradeByClasseAndSubject(@Param("classId") Long classId,
                                             @Param("subjectId") Long subjectId);

    // Calcule le taux de réussite pour une classe et une matière donnés (note >= 10).
    @Query("SELECT (COUNT(CASE WHEN ss.value >= 10 THEN 1 END) * 100.0 / COUNT(ss)) " +
            "FROM StudentSubmission ss " +
            "WHERE ss.student.classe.id = :classId " +
            "AND ss.evaluation.subject.id = :subjectId")
    Double calculatePassRateByClasseAndSubject(@Param("classId") Long classId,
                                               @Param("subjectId") Long subjectId);

    // Distribution des notes par intervalles (requête native pour PostgreSQL).
    @Query(value = "SELECT array_agg(cnt) FROM (" +
            "  SELECT count(*) as cnt FROM student_submissions ss " +
            "  WHERE ss.value >= 0 AND ss.value < 5 " +
            "    AND ss.student_id IN (SELECT id FROM students WHERE class_id = :classId) " +
            "    AND ss.evaluation_id IN (SELECT id FROM evaluations WHERE subject_id = :subjectId) " +
            "  UNION ALL " +
            "  SELECT count(*) FROM student_submissions ss " +
            "  WHERE ss.value >= 5 AND ss.value < 8 " +
            "    AND ss.student_id IN (SELECT id FROM students WHERE class_id = :classId) " +
            "    AND ss.evaluation_id IN (SELECT id FROM evaluations WHERE subject_id = :subjectId) " +
            "  UNION ALL " +
            "  SELECT count(*) FROM student_submissions ss " +
            "  WHERE ss.value >= 8 AND ss.value < 10 " +
            "    AND ss.student_id IN (SELECT id FROM students WHERE class_id = :classId) " +
            "    AND ss.evaluation_id IN (SELECT id FROM evaluations WHERE subject_id = :subjectId) " +
            "  UNION ALL " +
            "  SELECT count(*) FROM student_submissions ss " +
            "  WHERE ss.value >= 10 AND ss.value < 12 " +
            "    AND ss.student_id IN (SELECT id FROM students WHERE class_id = :classId) " +
            "    AND ss.evaluation_id IN (SELECT id FROM evaluations WHERE subject_id = :subjectId) " +
            "  UNION ALL " +
            "  SELECT count(*) FROM student_submissions ss " +
            "  WHERE ss.value >= 12 AND ss.value < 15 " +
            "    AND ss.student_id IN (SELECT id FROM students WHERE class_id = :classId) " +
            "    AND ss.evaluation_id IN (SELECT id FROM evaluations WHERE subject_id = :subjectId) " +
            "  UNION ALL " +
            "  SELECT count(*) FROM student_submissions ss " +
            "  WHERE ss.value >= 15 AND ss.value < 18 " +
            "    AND ss.student_id IN (SELECT id FROM students WHERE class_id = :classId) " +
            "    AND ss.evaluation_id IN (SELECT id FROM evaluations WHERE subject_id = :subjectId) " +
            "  UNION ALL " +
            "  SELECT count(*) FROM student_submissions ss " +
            "  WHERE ss.value >= 18 AND ss.value <= 20 " +
            "    AND ss.student_id IN (SELECT id FROM students WHERE class_id = :classId) " +
            "    AND ss.evaluation_id IN (SELECT id FROM evaluations WHERE subject_id = :subjectId) " +
            ") sub", nativeQuery = true)
    int[] getGradeDistributionByClasseAndSubject(@Param("classId") Long classId,
                                                 @Param("subjectId") Long subjectId);

    // Récupère une soumission pour un étudiant.
    @Query("SELECT ss FROM StudentSubmission ss WHERE ss.student.id = :studentId")
    Optional<StudentSubmission> findByStudentId(@Param("studentId") Long studentId);

    // Récupère les soumissions avec une note faible (value < 8) pour un étudiant.
    @Query("SELECT ss FROM StudentSubmission ss WHERE ss.student = :student AND ss.value < 8")
    List<StudentSubmission> findLowGrades(@Param("student") Student student);

    // Récupère les soumissions d'un étudiant triées par date décroissante.
    @Query("SELECT ss FROM StudentSubmission ss WHERE ss.student = :student ORDER BY ss.gradedAt DESC")
    List<StudentSubmission> findByStudentOrderByDateDesc(@Param("student") Student student);

    // Calcul des moyennes par matière pour un étudiant.
    @Query("SELECT ss.evaluation.subject.name as subject, AVG(ss.value) as average " +
            "FROM StudentSubmission ss WHERE ss.student = :student " +
            "GROUP BY ss.evaluation.subject.name")
    Map<String, Double> calculateSubjectAverages(@Param("student") Student student);

    // Calcul de la progression par matière pour un étudiant.
    @Query("SELECT ss.evaluation.subject.name as subject, AVG(ss.value) as average " +
            "FROM StudentSubmission ss WHERE ss.student = :student " +
            "GROUP BY ss.evaluation.subject.name")
    Map<String, List<Double>> calculateProgression(@Param("student") Student student);

    // Compte le nombre d'étudiants dans une classe (basé sur les soumissions).
    @Query("SELECT COUNT(ss) FROM StudentSubmission ss WHERE ss.student.classe.id = :classId")
    Long countStudentsInClass(@Param("classId") Long classId);

    // Compte le nombre de soumissions pour une évaluation donnée dans une classe et matière spécifiques.
    @Query("SELECT COUNT(ss) FROM StudentSubmission ss " +
            "WHERE ss.evaluation.subject.id = :subjectId " +
            "AND ss.evaluation.title = :title " +
            "AND ss.student.classe.id = :classId")
    Long countGradesForClass(@Param("subjectId") Long subjectId,
                             @Param("title") String title,
                             @Param("classId") Long classId);

    // Calcul de la moyenne d'une classe pour une évaluation donnée.
    @Query("SELECT AVG(ss.value) FROM StudentSubmission ss " +
            "WHERE ss.evaluation.subject.id = :subjectId " +
            "AND ss.evaluation.title = :title " +
            "AND ss.student.classe.id = :classId")
    Double calculateClassAverage(@Param("subjectId") Long subjectId,
                                 @Param("title") String title,
                                 @Param("classId") Long classId);

    // Calcul du taux de réussite pour une classe et une matière donnés (note >= 10).
    @Query("SELECT AVG(CASE WHEN ss.value >= 10 THEN 1 ELSE 0 END) " +
            "FROM StudentSubmission ss " +
            "WHERE ss.evaluation.subject.id = :subjectId " +
            "AND ss.evaluation.title = :title " +
            "AND ss.student.classe.id = :classId")
    Double calculateSuccessRate(@Param("subjectId") Long subjectId,
                                @Param("title") String title,
                                @Param("classId") Long classId);

    // Calcul du taux de réussite pour un étudiant.
    @Query("SELECT AVG(CASE WHEN ss.value >= 10 THEN 1 ELSE 0 END) " +
            "FROM StudentSubmission ss WHERE ss.student = :student")
    Double calculateSuccessRate(@Param("student") Student student);

    // Calcul de la moyenne d'un étudiant.
    @Query("SELECT AVG(ss.value) FROM StudentSubmission ss WHERE ss.student = :student")
    Double calculateAverageGrade(@Param("student") Student student);

    // Pagination des soumissions récentes pour un étudiant.
    @Query(value = "SELECT ss FROM StudentSubmission ss WHERE ss.student = :student ORDER BY ss.gradedAt DESC")
    Page<StudentSubmission> findRecentGrades(@Param("student") Student student, Pageable pageable);


    // Récupère les soumissions d'une classe pour une période donnée (past/upcoming).
    @Query("SELECT ss FROM StudentSubmission ss " +
            "WHERE ss.student.classe.id = :classId " +
            "AND ((:period = 'past' AND (TREAT(ss AS StudentHomework).homework.dueDate < CURRENT_DATE " +
            "      OR TREAT(ss AS StudentExam).exam.startTime < CURRENT_DATE)) " +
            "     OR (:period = 'upcoming' AND (TREAT(ss AS StudentHomework).homework.dueDate >= CURRENT_DATE " +
            "      OR TREAT(ss AS StudentExam).exam.startTime >= CURRENT_DATE)))")
    List<StudentSubmission> findByClasseAndPeriod(@Param("classId") Long classId,
                                                  @Param("period") String period);

    @Query("SELECT ss FROM StudentSubmission ss WHERE ss.student = :student ORDER BY ss.gradedAt DESC")
    List<StudentSubmission> findByStudentOrderByGradedAtDesc(@Param("student") Student student);



}
