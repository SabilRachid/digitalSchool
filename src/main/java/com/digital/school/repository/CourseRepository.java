package com.digital.school.repository;

import com.digital.school.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByClasse(Classe classe);

    List<Course> findByProfessor(User professor);

    @Query("SELECT c FROM Course c WHERE c.classe.id = :classId AND CAST(c.startTime as DATE) = :date ")
    Optional<Object> findByClassIdAndDate(Long classId, LocalDate date);

    @Query("SELECT c FROM Course c WHERE c.classe = :classe AND c.startTime BETWEEN :start AND :end")
    List<Course> findByClasseAndStartTimeBetween(Classe classe, LocalDateTime start, LocalDateTime end);

    @Query("SELECT c FROM Course c " +
            "JOIN c.classe.students s " +
            "WHERE s.id = :studentId " +
            "AND c.date = :today " +
            "ORDER BY c.startTime ASC")
    List<Course> findTodayScheduleByStudent(@Param("studentId") Long studentId, @Param("today") LocalDate today);
}