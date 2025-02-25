package com.digital.school.service.impl;

import com.digital.school.model.*;
import com.digital.school.repository.*;
import com.digital.school.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final ProfessorRepository professorRepository;
    private final ClasseRepository classeRepository;

    @Override
    public Page<Course> findAll(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    @Override
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Override
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    public List<Course> findTodaySchedule(Student student) {
        // On suppose que la méthode du repository prend l'ID de l'étudiant et la date d'aujourd'hui.
        return courseRepository.findTodayScheduleByStudent(student.getId(), LocalDate.now());
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
        Professor professor = professorRepository.findById(course.getProfessor().getId())
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
    public List<Map<String, Object>> findByProfessor(Professor professor) {
        return courseRepository.findByProfessor(professor).stream()
                .map(course -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", course.getId());
                    map.put("name", course.getName());
                    map.put("subject", course.getSubject().getName());
                    map.put("classe", course.getClasse().getName());
                    map.put("startTime", course.getStartTime());
                    map.put("endTime", course.getEndTime());
                    map.put("room", course.getRoom());
                    map.put("description", course.getDescription());
                    map.put("status", course.getStatus().name());
                    return map;
                })
                .collect(Collectors.toList());
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
                    map.put("status", course.getStatus().name());
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
    public List<Course> findByStudent(Student student) {
        return courseRepository.findByClasse(student.getClasse());
    }

    @Override
    public List<Map<String, Object>> getCourseResourcesAsMap(Long id) {
        // À implémenter selon votre logique de gestion des ressources (fichiers, liens, etc.)
        return List.of();
    }

    @Override
    public Map<String, String> generateMeetingLink(Long id) {
        // Implémenter la génération d'un lien de réunion pour le cours (exemple : via Zoom ou Google Meet)
        return Map.of("meetingLink", "https://meeting.example.com/" + id);
    }

    @Override
    public Object findTodaySchedule(Professor professor) {
        // Implémenter la récupération des cours d'aujourd'hui pour un professeur
        return courseRepository.findTodayScheduleByProfessor(professor.getId(), LocalDate.now());
    }

    @Override
    public Object findTodayCourses() {
        // Par exemple, retourner les cours programmés pour aujourd'hui pour tous les professeurs
        return courseRepository.findTodayCourses(LocalDate.now());
    }

    @Override
    public Course updateCourse(Long id, Course course, Professor professor) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        if (!existingCourse.getProfessor().equals(professor)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier ce cours");
        }
        // Mise à jour des champs essentiels
        existingCourse.setName(course.getName());
        existingCourse.setSubject(course.getSubject());
        existingCourse.setClasse(course.getClasse());
        existingCourse.setDate(course.getDate());
        existingCourse.setStartTime(course.getStartTime());
        existingCourse.setEndTime(course.getEndTime());
        existingCourse.setRoom(course.getRoom());
        existingCourse.setOnlineLink(course.getOnlineLink());
        existingCourse.setDescription(course.getDescription());
        existingCourse.setStatus(course.getStatus());
        return courseRepository.save(existingCourse);
    }


    @Override
    public void deleteCourseById(Long id, Professor professor) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        if (!existingCourse.getProfessor().equals(professor)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer ce cours");
        }
        courseRepository.delete(existingCourse);
    }

    @Override
    public Course updateCourseRoom(Long id, String newRoom, Professor professor) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        if (!existingCourse.getProfessor().equals(professor)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier ce cours");
        }
        existingCourse.setRoom(newRoom);
        return courseRepository.save(existingCourse);
    }


    @Override
    public Object findTodayCoursesForStudent(Long studentId) {
        // Implémenter selon vos besoins
        return courseRepository.findTodayCoursesForStudent(studentId, LocalDate.now());
    }

    @Override
    public List<Map<String, Object>> findByProfessorAndFilters(Professor professor, Long classeId, Long subjectId, String startDate, String endDate) {
        // Récupérer d'abord tous les cours du professeur
        List<Course> courses = courseRepository.findByProfessor(professor);

        // Appliquer le filtre sur la classe, si fourni
        if (classeId != null) {
            courses = courses.stream()
                    .filter(course -> course.getClasse() != null && course.getClasse().getId().equals(classeId))
                    .collect(Collectors.toList());
        }

        // Appliquer le filtre sur la matière, si fourni
        if (subjectId != null) {
            courses = courses.stream()
                    .filter(course -> course.getSubject() != null && course.getSubject().getId().equals(subjectId))
                    .collect(Collectors.toList());
        }

        // Appliquer le filtre sur la date de début, si fourni
        if (startDate != null && !startDate.isEmpty()) {
            LocalDate start = LocalDate.parse(startDate);
            courses = courses.stream()
                    .filter(course -> course.getDate() != null && !course.getDate().isBefore(start))
                    .collect(Collectors.toList());
        }

        // Appliquer le filtre sur la date de fin, si fourni
        if (endDate != null && !endDate.isEmpty()) {
            LocalDate end = LocalDate.parse(endDate);
            courses = courses.stream()
                    .filter(course -> course.getDate() != null && !course.getDate().isAfter(end))
                    .collect(Collectors.toList());
        }

        // Transformation des cours en Map<String, Object>
        return courses.stream().map(course -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", course.getId());
            map.put("name", course.getName());
            map.put("subjectName", course.getSubject().getName());
            map.put("classeName", course.getClasse().getName());
            map.put("startTime", course.getStartTime());
            map.put("endTime", course.getEndTime());
            map.put("room", course.getRoom());
            map.put("description", course.getDescription());
            map.put("status", course.getStatus().name());
            // Ajoutez d'autres champs si nécessaire (onlineLink, resourceCount, etc.)
            return map;
        }).collect(Collectors.toList());
    }



}
