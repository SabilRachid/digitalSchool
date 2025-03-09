package com.digital.school.repository;

import com.digital.school.model.Homework;
import com.digital.school.model.Student;
import com.digital.school.model.enumerated.EvaluationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface HomeworkRepository extends JpaRepository<Homework, Long> {

    // Devoirs pour lesquels il n'existe aucune EvaluationGrade pour l'étudiant et dont la date d'échéance est dépassée
    @Query("SELECT h FROM Homework h " +
            "LEFT JOIN EvaluationGrade eg ON eg.evaluation = h AND eg.student = :student " +
            "WHERE eg IS NULL AND h.dueDate < CURRENT_DATE " +
            "ORDER BY h.dueDate DESC")
    List<Homework> findPendingHomeworkByStudent(@Param("student") Student student);

    List<Homework> findByProfessorId(Long professorId);

    Optional<Homework> findByIdAndProfessor(Long id, com.digital.school.model.Professor professor);

    @Query("SELECT new map(" +
            "h.id as id, " +
            "h.title as title, " +
            "s.name as subjectName, " +
            "c.name as classeName, " +
            "h.dueDate as dueDate, " +
            "h.status as status) " +
            "FROM Homework h " +
            "JOIN h.subject s " +
            "JOIN h.classe c " +
            "WHERE (:classId IS NULL OR c.id = :classId) " +
            "AND (:year IS NULL OR FUNCTION('YEAR', h.dueDate) = :year) " +
            "AND (:month IS NULL OR FUNCTION('MONTH', h.dueDate) = :month)")
    List<Map<String, Object>> findAllAsMap(@Param("classId") Long classId,
                                           @Param("year") Integer year,
                                           @Param("month") Integer month);

    List<Homework> findByProfessor(com.digital.school.model.Professor professor);

    @Query("SELECT COUNT(h) FROM Homework h WHERE h.professor = :professor AND h.status = :status")
    int countPendingGradingByProfessor(@Param("professor") com.digital.school.model.Professor professor,
                                       @Param("status") EvaluationStatus status);

    @Query("SELECT COUNT(h) FROM Homework h WHERE h.professor = :professor")
    int countSubmissionsToday(@Param("professor") com.digital.school.model.Professor professor);

    @Query("SELECT h FROM Homework h WHERE h.professor = :professor AND h.status = :status ORDER BY h.dueDate ASC")
    List<Homework> findPendingGradingByProfessor(@Param("professor") com.digital.school.model.Professor professor,
                                                 @Param("status") EvaluationStatus status);

    // Récupérer les devoirs associés à un étudiant en se basant sur sa classe
    @Query("SELECT h FROM Homework h JOIN h.classe c JOIN c.students s WHERE s = :student")
    List<Homework> findByStudent(@Param("student") Student student);

    // Récupérer les devoirs d'un étudiant avec un statut spécifique, triés par date d'échéance décroissante.
    @Query("SELECT h FROM Homework h JOIN h.classe c JOIN c.students s " +
            "WHERE s = :student AND h.status = :status ORDER BY h.dueDate DESC")
    List<Homework> findByStudentAndStatusOrderByDueDateDesc(@Param("student") Student student,
                                                            @Param("status") EvaluationStatus status);



    @Query("SELECT COUNT(h) FROM Homework h JOIN h.classe c JOIN c.students s " +
            "WHERE s = :student AND h.status = :status")
    int countByStudentAndStatus(@Param("student") Student student,
                                @Param("status") EvaluationStatus status);

    @Query("SELECT COUNT(h) FROM Homework h JOIN h.classe c JOIN c.students s " +
            "WHERE s = :student AND h.status = com.digital.school.model.enumerated.EvaluationStatus.PUBLISHED")
    int countPendingHomework(@Param("student") Student student);

    @Query("SELECT h FROM Homework h JOIN h.classe c JOIN c.students s " +
            "WHERE s.id = :studentId AND h.dueDate >= :date " +
            "ORDER BY h.dueDate ASC")
    List<Homework> findUpcomingHomeworksByStudent(@Param("studentId") Long studentId,
                                                  @Param("date") LocalDate date);

    @Query("SELECT h FROM Homework h JOIN h.classe c JOIN c.students s " +
            "WHERE s.id = :studentId AND h.dueDate BETWEEN :startDate AND :endDate " +
            "ORDER BY h.dueDate ASC")
    List<Homework> findHomeworksByStudentBetweenDates(@Param("studentId") Long studentId,
                                                      @Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(h) FROM Homework h JOIN h.classe c JOIN c.students s " +
            "WHERE s.id = :childId AND h.status <> com.digital.school.model.enumerated.EvaluationStatus.SCHEDULED")
    long countByStudentId(Long childId);
}
