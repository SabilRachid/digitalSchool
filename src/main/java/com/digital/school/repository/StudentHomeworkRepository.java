package com.digital.school.repository;

import com.digital.school.model.StudentHomework;
import com.digital.school.model.Student;
import com.digital.school.model.enumerated.StudentSubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StudentHomeworkRepository extends JpaRepository<StudentHomework, Long> {

    @Query("SELECT sh FROM StudentHomework sh JOIN sh.homework h " +
            "WHERE sh.student.id = :studentId AND h.dueDate >= :currentDate " +
            "ORDER BY h.dueDate ASC")
    List<StudentHomework> findUpcomingHomeworksByStudent(@Param("studentId") Long studentId,
                                                         @Param("currentDate") LocalDate currentDate);

    @Query("SELECT sh FROM StudentHomework sh JOIN sh.homework h " +
            "WHERE sh.student.id = :studentId AND h.dueDate BETWEEN :startDate AND :endDate " +
            "ORDER BY h.dueDate ASC")
    List<StudentHomework> findHomeworksByStudentBetweenDates(@Param("studentId") Long studentId,
                                                             @Param("startDate") LocalDate startDate,
                                                             @Param("endDate") LocalDate endDate);

    // Filtre par statut en utilisant l'énumération, et tri décroissant par date d'échéance.
    @Query("SELECT sh FROM StudentHomework sh JOIN sh.homework h " +
            "WHERE sh.student = :student AND sh.status = :status " +
            "ORDER BY h.dueDate DESC")
    List<StudentHomework> findByStudentAndStatusOrderByDueDateDesc(@Param("student") Student student,
                                                                   @Param("status") StudentSubmissionStatus status);

    @Query("SELECT sh FROM StudentHomework sh WHERE sh.id = :id AND sh.student = :student")
    Optional<StudentHomework> findByIdAndStudent(@Param("id") Long id, @Param("student") Student student);

    @Query("SELECT sh FROM StudentHomework sh WHERE sh.student = :student")
    List<StudentHomework> findByStudent(@Param("student") Student student);

    @Query("SELECT COUNT(sh) FROM StudentHomework sh WHERE sh.student = :student AND sh.status = com.digital.school.model.enumerated.StudentSubmissionStatus.COMPLETED")
    int countCompletedHomework(@Param("student") Student student);

    @Query("SELECT COUNT(sh) FROM StudentHomework sh WHERE sh.student = :student AND sh.status = 'PENDING'")
    int countPendingHomework(@Param("student") Student student);

    @Query("SELECT COUNT(sh) FROM StudentHomework sh WHERE sh.student = :student AND sh.status = 'LATE'")
    int countLateHomework(@Param("student") Student student);

    @Query("SELECT COUNT(sh) FROM StudentHomework sh WHERE sh.student = :student")
    int countByStudent(@Param("student") Student student);
}
