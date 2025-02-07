package com.digital.school.service;

import com.digital.school.model.User;
import java.util.List;
import java.util.Map;

public interface ParentHomeworkService {
    List<Map<String, Object>> getChildrenHomework(User parent);
    Map<String, Object> getDetailedChildHomework(Long childId);
    Map<String, Object> getChildHomeworkStats(Long childId);
    void sendHomeworkReminder(Long homeworkId);
    boolean canAccessHomework(Long homeworkId, User parent);
}
