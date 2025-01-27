package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.digital.school.model.Attendance;
import com.digital.school.model.Course;
import com.digital.school.model.User;
import com.digital.school.model.enumerated.AttendanceStatus;
import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent(User student);
    List<Attendance> findByCourse(Course course);
    List<Attendance> findByStudentAndRecordedAtBetween(User student, LocalDateTime start, LocalDateTime end);
    List<Attendance> findByCourseAndRecordedAtBetween(Course course, LocalDateTime start, LocalDateTime end);
    
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

    @Query("SELECT COALESCE(AVG(CASE WHEN a.status = 'PRESENT' THEN 1.0 WHEN a.status = 'LATE' THEN 0.5 ELSE 0.0 END), 0.0) " +
            "FROM Attendance a " +
            "WHERE a.course.professor = :professor ")
    double calculateAverageAttendanceForProfessor(
            @Param("professor") User professor
    );
}