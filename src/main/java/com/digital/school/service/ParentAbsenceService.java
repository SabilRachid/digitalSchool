package com.digital.school.service;

import com.digital.school.model.User;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface ParentAbsenceService {
    List<Map<String, Object>> getChildrenAbsences(User parent);
    void submitJustification(Long absenceId, MultipartFile file, String reason, User parent);
    Map<String, Object> getChildAbsenceStats(Long childId);
    boolean canJustifyAbsence(Long absenceId, User parent);
}
