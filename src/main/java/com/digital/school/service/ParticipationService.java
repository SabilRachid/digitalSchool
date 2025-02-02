package com.digital.school.service;

import com.digital.school.model.Participation;
import com.digital.school.model.enumerated.ParticipationLevel;

import java.util.List;

public interface ParticipationService {
    List<Participation> getParticipationsByStudent(Long studentId);
    List<Participation> getParticipationsByCourse(Long courseId);
    Participation recordParticipation(Long studentId, Long courseId, String feedback, ParticipationLevel level);

    
    void deleteParticipation(Long id);
}
