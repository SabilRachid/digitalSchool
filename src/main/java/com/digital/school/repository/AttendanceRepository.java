package com.digital.school.repository;

import com.digital.school.model.Attendance;
import com.digital.school.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    /**
     * Récupère la fiche d'attendance pour un cours et une date donnée.
     */
    @Query("SELECT a FROM Attendance a WHERE a.course.id = :courseId AND a.dateEvent = :date")
    Attendance findByCourseAndDate(@Param("courseId") Long courseId, @Param("date") LocalDate date);

    /**
     * Récupère les données groupées pour l'affichage (par cours et par date),
     * en comptant le nombre d'enregistrements individuels (StudentAttendance) pour chaque fiche.
     */
    @Query("SELECT a.id as id, a.course, a.dateEvent, a.status ,COUNT(sa) " +
            "FROM Attendance a LEFT JOIN a.studentAttendances sa " +
            "WHERE a.course.professor.id = :professorId " +
            "AND (:classeId IS NULL OR a.course.classe.id = :classeId) " +
            "AND (a.dateEvent >= COALESCE(:startDate, CAST('1970-01-01' AS date))) " +
            "AND (a.dateEvent <= COALESCE(:endDate, CAST('2099-12-31' AS date))) " +
            "GROUP BY a.id, a.course, a.dateEvent, a.status")
    List<Object[]> findGroupedAttendances(@Param("professorId") Long professorId,
                                          @Param("classeId") Long classeId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);


    // Méthode d'existence (adaptée selon votre logique métier)
    boolean existsByCourseIdAndRecordedBy_Id(Long courseId, Long recordedById);

    Optional<Attendance> findByCourseId(Long courseId);
}
