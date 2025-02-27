package com.digital.school.service;

import com.digital.school.model.Event;
import com.digital.school.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
    Page<Event> findAll(Pageable pageable);
    Optional<Event> findById(Long id);
    Event save(Event event);
    Event update(Event event);
    void delete(Long id);
    List<Event> findEventsByDateRange(LocalDateTime start, LocalDateTime end, User user);
    List<Event> findUpcomingEvents(User user);
    List<Event> findEventsByParticipant(User participant);
    void addParticipant(Event event, User participant);
    void removeParticipant(Event event, User participant);
    Set<User> getParticipants(Event event);
    boolean isParticipant(Event event, User user);
    long countUpcomingEvents(User user);
    Map<String, Long> getEventStatistics(User user, LocalDateTime start, LocalDateTime end);
}
