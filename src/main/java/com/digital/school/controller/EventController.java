package com.digital.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.Event;
import com.digital.school.model.User;
import com.digital.school.service.EventService;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public ResponseEntity<List<Event>> getEvents(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @AuthenticationPrincipal User user) {
        try {
            List<Event> events = eventService.findEventsByDateRange(start, end, user);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(
            @RequestBody Event event,
            @AuthenticationPrincipal User user) {
        try {
            event.setCreatedBy(user);
            Event savedEvent = eventService.save(event);
            return ResponseEntity.ok(savedEvent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long id,
            @RequestBody Event event,
            @AuthenticationPrincipal User user) {
        try {
            event.setId(id);
            Event updatedEvent = eventService.update(event);
            return ResponseEntity.ok(updatedEvent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        try {
            eventService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}