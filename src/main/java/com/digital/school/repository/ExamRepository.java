package com.digital.school.repository;

import com.digital.school.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.digital.school.model.enumerated.ExamStatus;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByClasse(Classe classe);

    List<Exam> findExamsByProfessor(Professor professor);

    @Query("SELECT COUNT(e) FROM Exam e WHERE e.professor = :professor AND e.examDate > CURRENT_TIMESTAMP")
    int countUpcomingExamsByProfessor(@Param("professor") Professor professor);

    @Query("SELECT e FROM Exam e WHERE e.professor = :professor AND e.examDate > CURRENT_TIMESTAMP ORDER BY e.examDate")
    List<Exam> findByCreatedBy(Professor professor);

    @Query("SELECT e FROM Exam e WHERE e.professor = :professor AND e.examDate > CURRENT_TIMESTAMP ORDER BY e.examDate")
    List<Exam> findUpcomingExamsByProfessor(Professor professor);

    @Query("SELECT e FROM Exam e WHERE e.classe = :classe AND e.examDate >= :start AND e.examDate <= :end  ORDER BY e.examDate")
    List<Exam> findByClasseAndDateBetween(Classe classe, LocalDateTime start, LocalDateTime end);

    @Query("SELECT e FROM Exam e WHERE e.examDate > CURRENT_TIMESTAMP AND e.status = 'PENDING' AND e.classe = :classe")
    List<Exam> findUpcomingExams(Classe classe);

    @Query("SELECT COUNT(e) FROM Exam e WHERE e.examDate > CURRENT_TIMESTAMP AND e.status = 'PENDING' AND e.classe = :classe")
    int countUpcomingExams(Classe classe);

    List<Exam> findByProfessor_Id(Long professorId);
}