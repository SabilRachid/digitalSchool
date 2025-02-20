package com.digital.school.repository;

import com.digital.school.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.digital.school.model.enumerated.AttendanceStatus;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudent(User student);

    @Query("SELECT a FROM Attendance a WHERE a.student = :student AND a.recordedAt BETWEEN :start AND :end")
    List<Attendance> findByStudentAndRecordedAtBetween(@Param("student") Student student,
                                                       @Param("start") LocalDateTime start,
                                                       @Param("end") LocalDateTime end);

    @Query("SELECT a FROM Attendance a " +
            "WHERE (:classId IS NULL OR a.student.classe.id = :classId) " +
            "AND (:startDate IS NULL OR a.dateEvent >= :startDate) " +
            "AND (:endDate IS NULL OR a.dateEvent <= :endDate)")
    List<Attendance> findByClassIdAndDateRange(@Param("classId") Long classId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM Attendance a WHERE a.course = :course AND a.dateEvent = :date")
    List<Attendance> findByCourseAndDate(Course course, LocalDate date);

    @Query("SELECT (SUM(CASE WHEN (a.status = 'PRESENT' OR a.status = 'RETARD') THEN 1 ELSE 0 END) * 100.0 / COUNT(a)) FROM Attendance a")
    Double getAverageAttendance();

    @Query("SELECT (SUM(CASE WHEN a.status = 'EXCUSE' THEN 1 ELSE 0 END) * 100.0 / COUNT(a)) FROM Attendance a")
    Double getJustifiedAbsencesRate();

    @Query("SELECT (SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END) * 100.0 / COUNT(a)) FROM Attendance a")
    Double getUnjustifiedAbsencesRate();



    @Query("SELECT a.status, COUNT(a) FROM Attendance a WHERE a.course = :course GROUP BY a.status")
    List<Object[]> getAttendanceStatsByCourse(@Param("course") Course course);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student=:student")
	double calculateAttendanceRate(Student student);

    @Query("SELECT COALESCE(AVG(CASE WHEN a.status = 'PRESENT' THEN 1.0 WHEN a.status = 'RETARD' THEN 0.5 ELSE 0.0 END), 0.0) " +
            "FROM Attendance a WHERE a.course.professor = :professor ")
    double calculateAverageAttendanceForProfessor(
            @Param("professor") Professor professor
    );

    @Query("SELECT a.course, a.dateEvent, COUNT(a) FROM Attendance a " +
            "WHERE a.course.professor.id = :professorId " +
            "AND (:classeId IS NULL OR a.course.classe.id = :classeId) " +
            "AND (a.dateEvent >= COALESCE(:startDate, CAST('1970-01-01' AS DATE))) " +
            "AND (a.dateEvent <= COALESCE(:endDate, CAST('2099-12-31' AS DATE))) " +
            "GROUP BY a.course, a.dateEvent")
    List<Object[]> findGroupedAttendances(@Param("professorId") Long professorId,
                                          @Param("classeId") Long classeId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);


    @Query("SELECT a FROM Attendance a WHERE a.course.id = :courseId AND a.dateEvent = :date")
        List<Attendance> findByCourseAndDate(@Param("courseId") Long courseId, @Param("date") LocalDate date);



    boolean existsByCourseIdAndRecordedBy_Id(Long courseId, Long recordedById);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student AND a.status = 'ABSENT' AND a.justification IS NULL")
    long countUnjustifiedAbsences(Student student);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student")
    long countByStudent(Student student);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student AND a.status = :attendanceStatus")
    long countByStudentAndStatus(Student student, AttendanceStatus attendanceStatus);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student AND a.status = 'ABSENT' AND a.justification IS NULL")
    List<Map<String, Object>> findAbsenceDetails(Student student);

    @Query("SELECT a FROM Attendance a WHERE a.status = 'ABSENT' AND a.justification IS NULL")
    List<Attendance> findAbsenceDetails();


    @Query("SELECT a.course.subject.name as subject, COUNT(a) as count FROM Attendance a WHERE a.student = :student GROUP BY a.course.subject.name")
    Object getAbsencesBySubject(Student student);

    @Query("SELECT a.course.subject.name as subject, COUNT(a) as count FROM Attendance a WHERE a.student = :student GROUP BY a.course.subject.name")
    Object getMonthlyAbsenceTrend(Student student);

    @Query("SELECT a FROM Attendance a WHERE a.student = :student")
    List<Map<String, Object>> findLateDetails(Student student);
}