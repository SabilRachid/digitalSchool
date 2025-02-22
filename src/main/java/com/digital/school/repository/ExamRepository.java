package com.digital.school.repository;

import com.digital.school.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.digital.school.model.enumerated.ExamStatus;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    @Query("SELECT e FROM Exam e " +
            "JOIN e.classe c " +
            "JOIN c.students s " +
            "WHERE s.id = :studentId " +
            "  AND e.startTime >= :currentDate " +
            "ORDER BY e.startTime ASC")
    List<Exam> findUpcomingExamsByStudent(@Param("studentId") Long studentId,
                                          @Param("currentDate") LocalDate currentDate);

    List<Exam> findByClasse(Classe classe);

    List<Exam> findExamsByProfessor(Professor professor);

    @Query("SELECT COUNT(e) FROM Exam e WHERE e.professor = :professor AND e.startTime > CURRENT_TIMESTAMP")
    int countUpcomingExamsByProfessor(@Param("professor") Professor professor);

    @Query("SELECT e FROM Exam e WHERE e.professor = :professor AND e.startTime > CURRENT_TIMESTAMP ORDER BY e.startTime")
    List<Exam> findByCreatedBy(Professor professor);

    @Query("SELECT e FROM Exam e WHERE e.professor = :professor AND e.startTime > CURRENT_TIMESTAMP ORDER BY e.startTime")
    List<Exam> findUpcomingExamsByProfessor(Professor professor);

    @Query("SELECT e FROM Exam e WHERE e.classe = :classe AND e.startTime >= :start AND e.startTime <= :end  ORDER BY e.startTime")
    List<Exam> findByClasseAndDateBetween(Classe classe, LocalDateTime start, LocalDateTime end);

    @Query("SELECT e FROM Exam e WHERE e.startTime > CURRENT_TIMESTAMP AND e.status = 'PENDING' AND e.classe = :classe")
    List<Exam> findUpcomingExams(Classe classe);

    @Query("SELECT COUNT(e) FROM Exam e WHERE e.startTime > CURRENT_TIMESTAMP AND e.status = 'PENDING' AND e.classe = :classe")
    int countUpcomingExams(Classe classe);

    List<Exam> findByProfessor_Id(Long professorId);
}