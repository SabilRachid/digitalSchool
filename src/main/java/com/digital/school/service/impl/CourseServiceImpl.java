package com.digital.school.service.impl;

import com.digital.school.repository.ClasseRepository;
import com.digital.school.repository.SubjectRepository;
import com.digital.school.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.digital.school.model.Course;
import com.digital.school.model.Classe;
import com.digital.school.model.Subject;
import com.digital.school.model.User;
import com.digital.school.repository.CourseRepository;
import com.digital.school.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClasseRepository classeRepository;

    @Override
    public Page<Course> findAll(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    @Override
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    @Transactional
    public Course save(Course course) {
        if (course.getSubject() == null || course.getProfessor() == null || course.getClasse() == null) {
            throw new IllegalArgumentException("Les ID de la matière, du professeur et de la classe sont obligatoires");
        }

        // Charger les entités réelles depuis la base
        Subject subject = subjectRepository.findById(course.getSubject().getId())
                .orElseThrow(() -> new IllegalArgumentException("Matière non trouvée"));
        User professor = userRepository.findById(course.getProfessor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Professeur non trouvé"));
        Classe classe = classeRepository.findById(course.getClasse().getId())
                .orElseThrow(() -> new IllegalArgumentException("Classe non trouvée"));

        course.setSubject(subject);
        course.setProfessor(professor);
        course.setClasse(classe);

        return courseRepository.save(course);
    }

    @Override
    public void deleteById(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return courseRepository.existsById(id);
    }

    @Override
    public List<Course> findByClasse(Classe classe) {
        return courseRepository.findByClasse(classe);
    }

    @Override
    public List<Course> findByProfessor(User professor) {
        return courseRepository.findByProfessor(professor);
    }

    @Override
    public List<Course> findBySubject(Subject subject) {
        return courseRepository.findBySubject(subject);
    }

    @Override
    public List<Course> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return courseRepository.findByStartTimeBetween(start, end);
    }

    @Override
    public List<Course> findUpcomingCourses(User user) {
        LocalDateTime now = LocalDateTime.now();
        if (user.getRoles().stream().anyMatch(r -> r.getName().toString().equals("ROLE_PROFESSOR"))) {
            return courseRepository.findByProfessorAndStartTimeAfter(user, now);
        } else {
            return courseRepository.findByClasseAndStartTimeAfter(user.getClasse(), now);
        }
    }

    @Override
    public Map<String, Object> getCourseStatistics(Course course) {
        Map<String, Object> stats = new HashMap<>();
        // TODO: Implement course statistics
        return stats;
    }

    @Override
    public List<Map<String, Object>> findAllAsMap() {
        return courseRepository.findAll().stream()
            .map(course -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", course.getId());
                map.put("subject", Map.of(
                    "id", course.getSubject().getId(),
                    "name", course.getSubject().getName()
                ));
                map.put("professor", Map.of(
                    "id", course.getProfessor().getId(),
                    "firstName", course.getProfessor().getFirstName(),
                    "lastName", course.getProfessor().getLastName()
                ));
                map.put("class", Map.of(
                    "id", course.getClasse().getId(),
                    "name", course.getClasse().getName()
                ));
                map.put("startTime", course.getStartTime());
                map.put("endTime", course.getEndTime());
                map.put("room", course.getRoom());
                map.put("description", course.getDescription());
                return map;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> findAllBasicInfo() {
        return courseRepository.findAll().stream()
            .map(course -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", course.getId());
                map.put("name", String.format("%s - %s", 
                    course.getSubject().getName(),
                    course.getClasse().getName()));
                return map;
            })
            .collect(Collectors.toList());
    }

    @Override
    public Object findByStudent(User student) {
        return null;
    }

    @Override
    public List<Map<String, Object>> getCourseResourcesAsMap(Long id) {
        return List.of();
    }

    @Override
    public Map<String, String> generateMeetingLink(Long id) {
        return Map.of();
    }

    @Override
    public Object findTodaySchedule(User professor) {
        return null;
    }


}