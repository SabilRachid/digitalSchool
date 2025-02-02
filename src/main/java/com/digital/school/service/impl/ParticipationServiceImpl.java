package com.digital.school.service.impl;

import com.digital.school.dto.ParticipationDto;
import com.digital.school.model.Participation;
import com.digital.school.model.enumerated.ParticipationType;
import com.digital.school.repository.ParticipationRepository;
import com.digital.school.repository.UserRepository;
import com.digital.school.repository.CourseRepository;
import com.digital.school.service.ParticipationService;
import com.digital.school.model.enumerated.ParticipationLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParticipationServiceImpl implements ParticipationService {

    @Autowired
    private ParticipationRepository participationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;



    @Override
    public List<Participation> getParticipationsByStudent(Long studentId) {
        return participationRepository.findByStudentId(studentId);
    }

    @Override
    public List<Participation> getParticipationsByCourse(Long courseId) {
        return participationRepository.findByCourseId(courseId);
    }

    @Override
    public Participation recordParticipation(Long studentId, Long courseId, String feedback, ParticipationLevel level) {
        return null;
    }


    public ParticipationServiceImpl(ParticipationRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    //donne moi une implementation de getAllParticipations
    @Override
    public Collection<ParticipationDto> getAllParticipations() {
        return participationRepository.findAllProjected().stream()
                .map(objects -> new ParticipationDto(
                        (Long) objects[0],
                        (String) objects[1],
                        (String) objects[2],
                        (String) objects[3],
                        (String) objects[4],
                        (LocalDateTime) objects[5],
                        (ParticipationType) objects[6],
                        (String) objects[7]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ParticipationDto> getParticipationsByClassIdAndSubjectId(Long classId, Long subjectId) {
        return participationRepository.findByClassIdAndSubjectId(classId, subjectId).stream()
                .map(objects -> new ParticipationDto(
                        (Long) objects[0],
                        (String) objects[1],
                        (String) objects[2],
                        (String) objects[3],
                        (String) objects[4],
                        (LocalDateTime) objects[5],
                        (ParticipationType) objects[6],
                        (String) objects[7]
                ))
                .collect(Collectors.toList());
    }

    //updateParticipation
    @Override
    public Participation updateParticipation(Long id, Participation entity) {
        Participation existingParticipation = participationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Participation not found"));
        existingParticipation.setFeedback(entity.getFeedback());
        existingParticipation.setParticipationType(entity.getParticipationType());
        existingParticipation.setRecordedAt(entity.getRecordedAt());
        return participationRepository.save(existingParticipation);
    }

    @Override
    public Participation saveParticipation(Participation entity) {
        return participationRepository.save(entity);
    }

    //implemente deleteParticipation
    @Override
    public void deleteParticipation(Long id) {
        participationRepository.deleteById(id);
    }




}
