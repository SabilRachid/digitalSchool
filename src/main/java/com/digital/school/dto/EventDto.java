package com.digital.school.dto;

import com.digital.school.model.Event;

import java.time.Duration;
import java.time.LocalDateTime;

public class EventDto {
    private String title;
    private String description;
    private String eventType;
    private String timeAgo;

    public static EventDto fromEntity(Event event) {
        EventDto dto = new EventDto();
        dto.title = event.getTitle();
        dto.description = event.getDescription();
        dto.eventType = event.getType().name();
        dto.timeAgo = formatTimeAgo(event.getStartTime());
        return dto;
    }

    private static String formatTimeAgo(LocalDateTime startTime) {
        Duration duration = Duration.between(startTime, LocalDateTime.now());
        long minutes = duration.toMinutes();
        if (minutes < 60) {
            return "Il y a " + minutes + " minutes";
        } else {
            long hours = duration.toHours();
            return "Il y a " + hours + " heures";
        }
    }
}
