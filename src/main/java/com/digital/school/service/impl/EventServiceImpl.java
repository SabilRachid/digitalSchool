package com.digital.school.service.impl;

import com.digital.school.model.enumerated.EventType;
import com.digital.school.repository.EventRepository;
import com.digital.school.service.EventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.digital.school.model.Event;
import com.digital.school.model.User;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Override
    public Page<Event> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    @Override
    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    @Transactional
    public Event save(Event event) {
        // Définir les couleurs par défaut si non spécifiées
        if (event.getBackgroundColor() == null) {
            switch (event.getType()) {
                case COURSE:
                    event.setBackgroundColor("#4C51BF");
                    break;
                case EXAM:
                    event.setBackgroundColor("#ED8936");
                    break;
                case EVENT:
                    event.setBackgroundColor("#48BB78");
                    break;
                case MEETING:
                    event.setBackgroundColor("#4299E1");
                    break;
                default:
                    event.setBackgroundColor("#718096");
            }
        }

        if (event.getTextColor() == null) {
            event.setTextColor("#FFFFFF");
        }

        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public Event update(Event event) {
        if (!eventRepository.existsById(event.getId())) {
            throw new IllegalArgumentException("Event not found with id: " + event.getId());
        }
        return save(event);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        eventRepository.deleteById(id);
    }

    @Override
    public List<Event> findEventsByDateRange(LocalDateTime start, LocalDateTime end, User user) {
        return eventRepository.findByStartTimeBetweenAndParticipantsContainingAndTypeNot(start, end, user, EventType.HOMEWORK);
    }


    @Override
    public List<Event> findUpcomingEvents(User user) {
        return eventRepository.findByStartTimeAfterAndParticipantsContainingOrderByStartTime(
                LocalDateTime.now(), user);
    }

    @Override
    public List<Event> findEventsByParticipant(User participant) {
        return eventRepository.findByParticipantsContaining(participant);
    }

    @Override
    @Transactional
    public void addParticipant(Event event, User participant) {
        event.getParticipants().add(participant);
        eventRepository.save(event);
    }

    @Override
    @Transactional
    public void removeParticipant(Event event, User participant) {
        event.getParticipants().remove(participant);
        eventRepository.save(event);
    }

    @Override
    public Set<User> getParticipants(Event event) {
        return event.getParticipants();
    }

    @Override
    public boolean isParticipant(Event event, User user) {
        return event.getParticipants().contains(user);
    }

    @Override
    public long countUpcomingEvents(User user) {
        return eventRepository.countByStartTimeAfterAndParticipantsContaining(
                LocalDateTime.now(), user);
    }

    @Override
    public Map<String, Long> getEventStatistics(User user, LocalDateTime start, LocalDateTime end) {
        Map<String, Long> stats = new HashMap<>();

        stats.put("totalEvents", eventRepository.countByStartTimeBetweenAndParticipantsContaining(
                start, end, user));

        stats.put("createdEvents", eventRepository.countByCreatedByAndStartTimeBetween(
                user, start, end));

        stats.put("upcomingEvents", eventRepository.countByStartTimeAfterAndParticipantsContaining(
                LocalDateTime.now(), user));

        return stats;
    }
}
