package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.*;
import com.digital.school.repository.*;
import com.digital.school.service.ParentScheduleService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ParentScheduleServiceImpl implements ParentScheduleService {

    @Autowired
    private ParentStudentRepository parentStudentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ExamRepository examRepository;

    @Override
    public List<Map<String, Object>> getChildrenSchedule(User parent) {
        return parentStudentRepository.findByParent(parent).stream()
                .map(association -> {
                    Student child = association.getStudent();
                    Map<String, Object> childSchedule = new HashMap<>();
                    childSchedule.put("childId", child.getId());
                    childSchedule.put("childName", child.getFirstName() + " " + child.getLastName());
                    childSchedule.put("class", child.getClasse().getName());
                    childSchedule.put("todayClasses", getTodayClasses(child));
                    childSchedule.put("upcomingEvents", getUpcomingEvents(child));
                    return childSchedule;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getChildSchedule(Long childId, LocalDateTime start, LocalDateTime end) {
        Student child = parentStudentRepository.findByStudentId(childId)
                .map(ParentStudent::getStudent)
                .orElseThrow(() -> new RuntimeException("Enfant non trouvé"));

        List<Map<String, Object>> schedule = new ArrayList<>();

        // Ajouter les cours
        courseRepository.findByClasseAndStartTimeBetween(child.getClasse(), start, end)
                .forEach(course -> {
                    Map<String, Object> event = new HashMap<>();
                    event.put("type", "COURSE");
                    event.put("title", course.getSubject().getName());
                    event.put("start", course.getStartTime());
                    event.put("end", course.getEndTime());
                    event.put("location", course.getRoom());
                    event.put("professor", course.getProfessor().getLastName());
                    schedule.add(event);
                });

        // Ajouter les examens
        examRepository.findByClasseAndDateBetween(child.getClasse(), start, end)
                .forEach(exam -> {
                    Map<String, Object> event = new HashMap<>();
                    event.put("type", "EXAM");
                    event.put("title", "Examen - " + exam.getSubject().getName());
                    event.put("start", exam.getStartTime());
                    event.put("end", exam.getEndTime().plusMinutes(exam.getDuration()));
                    event.put("location", exam.getRoom());
                    schedule.add(event);
                });

        // Ajouter les événements
        eventRepository.findByParticipantAndDateBetween(child, start, end)
                .forEach(event -> {
                    Map<String, Object> eventMap = new HashMap<>();
                    eventMap.put("type", "EVENT");
                    eventMap.put("title", event.getTitle());
                    eventMap.put("start", event.getStartTime());
                    eventMap.put("end", event.getEndTime());
                    eventMap.put("location", event.getLocation());
                    schedule.add(eventMap);
                });

        return schedule;
    }

    @Override
    public List<Map<String, Object>> getChildEvents(Long childId) {
        Student child = parentStudentRepository.findByStudentId(childId)
                .map(ParentStudent::getStudent)
                .orElseThrow(() -> new RuntimeException("Enfant non trouvé"));

        List<Map<String, Object>> events = new ArrayList<>();

        // Ajouter les examens à venir
        examRepository.findUpcomingExams(child.getClasse()).forEach(exam -> {
            Map<String, Object> event = new HashMap<>();
            event.put("type", "EXAM");
            event.put("title", "Examen - " + exam.getSubject().getName());
            event.put("date", exam.getDate());
            event.put("subject", exam.getSubject().getName());
            events.add(event);
        });

        // Ajouter les événements spéciaux
        eventRepository.findUpcomingEvents(child).forEach(event -> {
            Map<String, Object> eventMap = new HashMap<>();
            eventMap.put("type", "EVENT");
            eventMap.put("title", event.getTitle());
            eventMap.put("date", event.getStartTime());
            eventMap.put("description", event.getDescription());
            events.add(eventMap);
        });

        // Trier par date
        events.sort((e1, e2) -> ((LocalDateTime) e1.get("date"))
                .compareTo((LocalDateTime) e2.get("date")));

        return events;
    }

    @Override
    public Map<String, Object> getChildScheduleStats(Long childId) {
        Student child = parentStudentRepository.findByStudentId(childId)
                .map(ParentStudent::getStudent)
                .orElseThrow(() -> new RuntimeException("Enfant non trouvé"));

        Map<String, Object> stats = new HashMap<>();

        // Heures de cours par semaine
        stats.put("weeklyHours", calculateWeeklyHours(child));

        // Répartition par matière
        stats.put("subjectDistribution", calculateSubjectDistribution(child));

        // Examens à venir
        stats.put("upcomingExams", examRepository.countUpcomingExams(child.getClasse()));

        // Événements à venir
        stats.put("upcomingEvents", eventRepository.countUpcomingEvents(child));

        return stats;
    }

    private List<Map<String, Object>> getTodayClasses(Student child) {
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0);
        LocalDateTime end = start.plusDays(1);

        return courseRepository.findByClasseAndStartTimeBetween(child.getClasse(), start, end)
                .stream()
                .map(course -> {
                    Map<String, Object> courseMap = new HashMap<>();
                    courseMap.put("subject", course.getSubject().getName());
                    courseMap.put("startTime", course.getStartTime());
                    courseMap.put("endTime", course.getEndTime());
                    courseMap.put("room", course.getRoom());
                    courseMap.put("professor", course.getProfessor().getLastName());
                    return courseMap;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getUpcomingEvents(Student child) {
        List<Map<String, Object>> events = new ArrayList<>();

        // Examens à venir
        examRepository.findUpcomingExams(child.getClasse())
                .stream()
                .limit(5)
                .forEach(exam -> {
                    Map<String, Object> event = new HashMap<>();
                    event.put("type", "EXAM");
                    event.put("title", exam.getTitle());
                    event.put("date", exam.getDate());
                    event.put("subject", exam.getSubject().getName());
                    events.add(event);
                });

        // Autres événements
        eventRepository.findUpcomingEvents(child)
                .stream()
                .limit(5)
                .forEach(event -> {
                    Map<String, Object> eventMap = new HashMap<>();
                    eventMap.put("type", "EVENT");
                    eventMap.put("title", event.getTitle());
                    eventMap.put("date", event.getStartTime());
                    eventMap.put("description", event.getDescription());
                    events.add(eventMap);
                });

        // Trier par date
        events.sort((e1, e2) -> ((LocalDateTime) e1.get("date"))
                .compareTo((LocalDateTime) e2.get("date")));

        return events.stream().limit(5).collect(Collectors.toList());
    }

    private double calculateWeeklyHours(Student child) {
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0);
        LocalDateTime end = start.plusDays(7);

        return courseRepository.findByClasseAndStartTimeBetween(child.getClasse(), start, end)
                .stream()
                .mapToDouble(course -> {
                    long minutes = java.time.Duration.between(
                            course.getStartTime(),
                            course.getEndTime()
                    ).toMinutes();
                    return minutes / 60.0;
                })
                .sum();
    }

    private Map<String, Double> calculateSubjectDistribution(Student child) {
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0);
        LocalDateTime end = start.plusDays(7);

        return courseRepository.findByClasseAndStartTimeBetween(child.getClasse(), start, end)
                .stream()
                .collect(Collectors.groupingBy(
                        course -> course.getSubject().getName(),
                        Collectors.summingDouble(course -> {
                            long minutes = java.time.Duration.between(
                                    course.getStartTime(),
                                    course.getEndTime()
                            ).toMinutes();
                            return minutes / 60.0;
                        })
                ));
    }
}
