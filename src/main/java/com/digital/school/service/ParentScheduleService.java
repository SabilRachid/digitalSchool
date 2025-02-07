package com.digital.school.service;

import com.digital.school.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ParentScheduleService {
    List<Map<String, Object>> getChildrenSchedule(User parent);
    List<Map<String, Object>> getChildSchedule(Long childId, LocalDateTime start, LocalDateTime end);
    List<Map<String, Object>> getChildEvents(Long childId);
    Map<String, Object> getChildScheduleStats(Long childId);
}
