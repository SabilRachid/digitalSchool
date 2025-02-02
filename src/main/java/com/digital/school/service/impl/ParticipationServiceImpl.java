package com.digital.school.service.impl;

import com.digital.school.model.Participation;
import com.digital.school.model.User;
import com.digital.school.model.Course;
import com.digital.school.repository.ParticipationRepository;
import com.digital.school.repository.UserRepository;
import com.digital.school.repository.CourseRepository;
import com.digital.school.service.ParticipationService;
import com.digital.school.model.enumerated.ParticipationLevel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipationServiceImpl implements ParticipationService {

    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public ParticipationServiceImpl(ParticipationRepository participationRepository, UserRepository userRepository, CourseRepository courseRepository) {
        this.participationRepository = participationRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

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
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        Participation participation = new Participation();
        participation.setStudent(student);
        participation.setCourse(course);
        participation.setLevel(level);
        participation.setFeedback(feedback);

        return participationRepository.save(participation);
    }

    //implemente deleteParticipation
    @Override
    public void deleteParticipation(Long id) {
        participationRepository.deleteById(id);
    }


}
