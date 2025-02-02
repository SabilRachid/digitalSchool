package com.digital.school.service.impl;

import com.digital.school.model.Participation;
import com.digital.school.model.User;
import com.digital.school.model.Course;
import com.digital.school.repository.ParticipationRepository;
import com.digital.school.repository.UserRepository;
import com.digital.school.repository.CourseRepository;
import com.digital.school.service.ParticipationService;
import com.digital.school.model.enumerated.ParticipationLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

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

    @Override
    public Collection<Participation> getAllParticipations() {
        return participationRepository.findAllProjected();
    }

    @Override
    public Collection<Participation> getParticipationsByClassAndSubject(Long classId, Long subjectId) {
        return participationRepository.findByClassAndSubject(classId, subjectId);
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
