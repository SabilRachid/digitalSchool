package com.digital.school.repository;


import com.digital.school.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.digital.school.model.Meeting;
import com.digital.school.model.User;
import java.time.LocalDateTime;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByOrganizer(User organizer);
    
    @Query("SELECT m FROM Meeting m WHERE m.organizer = :organizer AND m.startTime > :now ORDER BY m.startTime")
    List<Meeting> findUpcomingMeetingsByOrganizer(
        @Param("organizer") User organizer,
        @Param("now") LocalDateTime now
    );
    
    @Query("SELECT COUNT(m) FROM Meeting m " +
           "WHERE m.organizer = :organizer " +
           "AND ((m.startTime BETWEEN :start AND :end) " +
           "OR (m.endTime BETWEEN :start AND :end)) " +
           "AND (m.id != :meetingId OR :meetingId IS NULL)")
    long countOverlappingMeetings(
        @Param("organizer") User organizer,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        @Param("meetingId") Long meetingId
    );

    @Query("SELECT COUNT(m) FROM Meeting m WHERE :student MEMBER OF m.participants  AND m.startTime < :now")
    long countPendingMeetings(Student student);

    @Query("SELECT m FROM Meeting m WHERE :student MEMBER OF m.participants AND m.startTime > :now ORDER BY m.startTime")
    List<Meeting> findUpcomingMeetingsByStudent(Student student);
}
