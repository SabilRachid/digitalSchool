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


    // Récupère les cours d'un professeur (en utilisant l'entité Professor)
    List<Course> findByProfessor(Professor professor);

    // Récupère un cours pour une classe à une date précise (en comparant la date de startTime)
    @Query("SELECT c FROM Course c WHERE c.classe.id = :classId AND FUNCTION('DATE', c.startTime) = :date")
    Optional<Course> findByClassIdAndDate(@Param("classId") Long classId, @Param("date") LocalDate date);

    @Query("SELECT c FROM Course c WHERE c.classe = :classe AND c.startTime BETWEEN :start AND :end")
    List<Course> findByClasseAndStartTimeBetween(@Param("classe") Classe classe,
                                                 @Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);

    // Récupère le planning d'aujourd'hui pour un étudiant (en se basant sur le champ 'date')
    @Query("SELECT c FROM Course c " +
            "JOIN c.classe.students s " +
            "WHERE s.id = :studentId " +
            "AND c.date = :today " +
            "ORDER BY c.startTime ASC")
    List<Course> findTodayScheduleByStudent(@Param("studentId") Long studentId,
                                            @Param("today") LocalDate today);

    List<Course> findByClasse(Classe classe);

    List<Course> findByProfessor(User professor);

        @Query("SELECT c FROM Course c WHERE c.professor.id = :professorId AND c.date = :today ORDER BY c.startTime ASC")
        List<Course> findTodayScheduleByProfessor(@Param("professorId") Long professorId, @Param("today") LocalDate today);

        @Query("SELECT c FROM Course c WHERE c.date = :today ORDER BY c.startTime ASC")
        List<Course> findTodayCourses(@Param("today") LocalDate today);

        @Query("SELECT c FROM Course c JOIN c.classe.students s WHERE s.id = :studentId AND c.date = :today ORDER BY c.startTime ASC")
        List<Course> findTodayCoursesForStudent(@Param("studentId") Long studentId, @Param("today") LocalDate today);

        @Query("SELECT c FROM Course c WHERE c.professor.id = :professorId " +
                "AND c.classe.id = :classId " +
                "AND c.date = :date")
        List<Course> findCoursesForProfessorByClassAndDate(@Param("professorId") Long professorId,
                                                           @Param("classId") Long classId,
                                                           @Param("date") LocalDate date);
}