package com.digital.school.repository;

import com.digital.school.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.digital.school.model.Attendance;
import com.digital.school.model.Course;
import com.digital.school.model.User;
import com.digital.school.model.enumerated.AttendanceStatus;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent(User student);

    @Query("SELECT a FROM Attendance a WHERE a.course = :course")
    List<Attendance> findByCourse(Course course);

    List<Attendance> findByStudentAndRecordedAtBetween(User student, LocalDateTime start, LocalDateTime end);
    List<Attendance> findByCourseAndRecordedAtBetween(Course course, LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM Attendance a WHERE a.course = :course AND a.dateEvent = :date")
    List<Attendance> findByCourseAndDate(Course course, LocalDate date);

    @Query("SELECT (SUM(CASE WHEN (a.status = 'PRESENT' OR a.status = 'RETARD') THEN 1 ELSE 0 END) * 100.0 / COUNT(a)) FROM Attendance a")
    Double getAverageAttendance();

    @Query("SELECT (SUM(CASE WHEN a.status = 'EXCUSE' THEN 1 ELSE 0 END) * 100.0 / COUNT(a)) FROM Attendance a")
    Double getJustifiedAbsencesRate();

    @Query("SELECT (SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END) * 100.0 / COUNT(a)) FROM Attendance a")
    Double getUnjustifiedAbsencesRate();

    long countByStudentAndCourse(User student, Course course);
    
    long countByStudentAndStatusAndCourse(
        @Param("student") User student, 
        @Param("status") AttendanceStatus status, 
        @Param("course") Course course
    );
    
    @Query("SELECT a.status, COUNT(a) FROM Attendance a WHERE a.course = :course GROUP BY a.status")
    List<Object[]> getAttendanceStatsByCourse(@Param("course") Course course);
    
    @Query("SELECT COUNT(a) FROM Attendance a")
	double calculateAttendanceRate(User student);

    @Query("SELECT COALESCE(AVG(CASE WHEN a.status = 'PRESENT' THEN 1.0 WHEN a.status = 'RETARD' THEN 0.5 ELSE 0.0 END), 0.0) " +
            "FROM Attendance a WHERE a.course.professor = :professor ")
    double calculateAverageAttendanceForProfessor(
            @Param("professor") User professor
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
    long countUnjustifiedAbsences(User child);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student")
    long countByStudent(Student student);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student AND a.status = :attendanceStatus")
    long countByStudentAndStatus(Student student, AttendanceStatus attendanceStatus);

    @Query("SELECT a FROM Attendance a WHERE a.student = :student AND a.status = 'ABSENT' AND a.justification IS NOT NULL")
    List<Attendance> findAbsenceDetails(Student student);

    @Query("SELECT a FROM Attendance a WHERE a.status = 'ABSENT' AND a.justification IS NULL")
    List<Attendance> findAbsenceDetails();
}