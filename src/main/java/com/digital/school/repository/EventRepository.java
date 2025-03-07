package com.digital.school.repository;

import com.digital.school.model.Student;
import com.digital.school.model.enumerated.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digital.school.model.Event;
import com.digital.school.model.User;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {


    @Query("SELECT e FROM Event e " +
            "WHERE e.startTime BETWEEN :start AND :end " +
            "AND :user MEMBER OF e.participants " +
            "AND e.type <> :excludedType")
    List<Event> findByStartTimeBetweenAndParticipantsContainingAndTypeNot(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("user") User user,
            @Param("excludedType") EventType excludedType);


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


    @Query("SELECT COUNT(e) FROM Event e WHERE e.startTime > CURRENT_TIMESTAMP AND :student MEMBER OF e.participants")
    int countUpcomingEvents(Student student);

    // Récupérer les 6 dernières activités
    @Query("SELECT e FROM Event e ORDER BY e.startTime DESC LIMIT 6")
    List<Event> findLastEvents();

    @Query("SELECT e FROM Event e WHERE e.startTime > CURRENT_TIMESTAMP AND e.type='EXAM' AND :student MEMBER OF e.participants")
    List<Event> findUpcomingExams(Student student);

    @Query("SELECT e FROM Event e WHERE e.startTime > :start AND e.startTime < :end AND e.type='COURSE' AND :student MEMBER OF e.participants")
    List<Event> findByParticipantAndDateBetween(Student student, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.startTime > CURRENT_TIMESTAMP AND e.type='EXAM' AND :student MEMBER OF e.participants")
    int countUpcomingExams(Student student);

    @Query("SELECT e FROM Event e WHERE e.startTime > CURRENT_TIMESTAMP AND :student MEMBER OF e.participants")
    List<Event> findUpcomingEvents(Student student);
}