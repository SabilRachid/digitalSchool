package com.digital.school.repository;

import com.digital.school.model.Professor;
import com.digital.school.model.Student;
import com.digital.school.model.StudentAttendance;
import com.digital.school.model.enumerated.StudentAttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface StudentAttendanceRepository extends JpaRepository<StudentAttendance, Long> {

    List<StudentAttendance> findByStudent(Student student);

    @Query("SELECT sa FROM StudentAttendance sa WHERE sa.student = :student AND sa.recordedAt BETWEEN :start AND :end")
    List<StudentAttendance> findByStudentAndRecordedAtBetween(@Param("student") Student student,
                                                              @Param("start") LocalDateTime start,
                                                              @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(sa) FROM StudentAttendance sa WHERE sa.student = :student")
    long countByStudent(@Param("student") Student student);

    @Query("SELECT COUNT(sa) FROM StudentAttendance sa WHERE sa.student = :student AND sa.status = :attendanceStatus")
    long countByStudentAndStatus(@Param("student") Student student, @Param("attendanceStatus") StudentAttendanceStatus attendanceStatus);

    @Query("SELECT COUNT(sa) FROM StudentAttendance sa WHERE sa.student = :student AND sa.status = 'ABSENT' AND sa.justification IS NULL")
    long countUnjustifiedAbsences(@Param("student") Student student);

    @Query("SELECT sa.attendance.course.subject.name as subject, COUNT(sa) as count " +
            "FROM StudentAttendance sa WHERE sa.student = :student GROUP BY sa.attendance.course.subject.name")
    Object getAbsencesBySubject(@Param("student") Student student);

    @Query("SELECT sa.attendance.course.subject.name as subject, COUNT(sa) as count " +
            "FROM StudentAttendance sa WHERE sa.student = :student GROUP BY sa.attendance.course.subject.name")
    Object getMonthlyAbsenceTrend(@Param("student") Student student);

    // Méthode pour calculer le taux d'assiduité moyen
    @Query("SELECT COALESCE(AVG(CASE WHEN sa.status = 'PRESENT' THEN 1.0 WHEN sa.status = 'RETARD' THEN 0.5 ELSE 0.0 END), 0.0) " +
            "FROM StudentAttendance sa")
    Double getAverageAttendance();

    // Taux d'absences justifiées (en pourcentage)
    @Query("SELECT COALESCE((SUM(CASE WHEN sa.status = 'EXCUSE' THEN 1 ELSE 0 END) * 100.0 / COUNT(sa)), 0.0) " +
            "FROM StudentAttendance sa")
    Double getJustifiedAbsencesRate();

    // Taux d'absences non justifiées (en pourcentage)
    @Query("SELECT COALESCE((SUM(CASE WHEN sa.status = 'ABSENT' THEN 1 ELSE 0 END) * 100.0 / COUNT(sa)), 0.0) " +
            "FROM StudentAttendance sa")
    Double getUnjustifiedAbsencesRate();

    @Query("SELECT COALESCE(AVG(CASE WHEN sa.status = 'PRESENT' THEN 1.0 WHEN sa.status = 'RETARD' THEN 0.5 ELSE 0.0 END), 0.0) " +
            "FROM StudentAttendance sa WHERE sa.attendance.course.professor = :professor")
    double calculateAverageAttendanceForProfessor(@Param("professor") Professor professor);


    /**
     * Récupère les détails des absences non justifiées pour un étudiant.
     * Chaque résultat est renvoyé sous forme de Map avec les clés : id, date, subject, professor, status, justification.
     */
    @Query("SELECT new map(" +
            "sa.id as id, " +
            "sa.recordedAt as date, " +
            "sa.attendance.course.subject.name as subject, " +
            "sa.attendance.course.professor.lastName as professor, " +
            "sa.status as status, " +
            "sa.justification as justification) " +
            "FROM StudentAttendance sa " +
            "WHERE sa.student = :student AND sa.status = 'ABSENT' AND sa.justification IS NULL")
    List<Map<String, Object>> findAbsenceDetails(@Param("student") Student student);

    /**
     * Récupère les détails des retards pour un étudiant.
     * Chaque résultat est renvoyé sous forme de Map avec les clés : id, date, subject, professor, status.
     */
    @Query("SELECT new map(" +
            "sa.id as id, " +
            "sa.recordedAt as date, " +
            "sa.attendance.course.subject.name as subject, " +
            "sa.attendance.course.professor.lastName as professor, " +
            "sa.status as status) " +
            "FROM StudentAttendance sa " +
            "WHERE sa.student = :student AND sa.status = 'RETARD'")
    List<Map<String, Object>> findLateDetails(@Param("student") Student student);

    @Query("SELECT COALESCE((SUM(CASE WHEN sa.status = 'ABSENT' THEN 1 ELSE 0 END) * 100.0 / COUNT(sa)), 0.0) " +
            "FROM StudentAttendance sa WHERE sa.student = :student")
    double calculateAttendanceRate(@Param("student") Student student);

}
