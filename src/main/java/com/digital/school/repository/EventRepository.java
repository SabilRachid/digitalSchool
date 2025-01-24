package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digital.school.model.Event;
import com.digital.school.model.User;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStartTimeBetweenAndParticipantsContaining(
        LocalDateTime start, LocalDateTime end, User participant);
    
    List<Event> findByStartTimeAfterAndParticipantsContainingOrderByStartTime(
        LocalDateTime startTime, User participant);
    
    List<Event> findByParticipantsContaining(User participant);
    
    long countByStartTimeAfterAndParticipantsContaining(
        LocalDateTime startTime, User participant);
    
    long countByStartTimeBetweenAndParticipantsContaining(
        LocalDateTime start, LocalDateTime end, User participant);
    
    long countByCreatedByAndStartTimeBetween(
        User createdBy, LocalDateTime start, LocalDateTime end);

    @Query("SELECT e FROM Event e")
	List<Event> findUpcomingEventsByStudent(User student);

    @Query("SELECT count(*) FROM Event e")
	int countUpcomingExams(User student);
}