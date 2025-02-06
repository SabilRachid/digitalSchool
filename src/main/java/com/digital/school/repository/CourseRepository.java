package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.digital.school.model.Course;
import com.digital.school.model.Classe;
import com.digital.school.model.Subject;
import com.digital.school.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByClasse(Classe classe);
    List<Course> findByProfessor(User professor);
    List<Course> findBySubject(Subject subject);
    List<Course> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT c FROM Course c WHERE c.professor = :professor AND c.startTime > :now ORDER BY c.startTime")
    List<Course> findByProfessorAndStartTimeAfter(@Param("professor") User professor, @Param("now") LocalDateTime now);
    
    @Query("SELECT c FROM Course c WHERE c.classe = :classe AND c.startTime > :now ORDER BY c.startTime")
    List<Course> findByClasseAndStartTimeAfter(@Param("classe") Classe classe, @Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.professor = :professor AND c.startTime BETWEEN :start AND :end")
    long countByProfessorAndTimeRange(@Param("professor") User professor, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.classe = :classe AND c.startTime BETWEEN :start AND :end")
    long countByClasseAndTimeRange(@Param("classe") Classe classe, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT c FROM Course c WHERE c.classe.id = :classId AND CAST(c.startTime as DATE) = :date ")
    Optional<Object> findByClassIdAndDate(Long classId, LocalDate date);
}