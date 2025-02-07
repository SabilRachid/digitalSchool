```java
package com.digital.school.controller.parent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.User;
import com.digital.school.service.ParentHomeworkService;

import java.util.Map;

@Controller
@RequestMapping("/parent/homework")
public class ParentHomeworkController {

    @Autowired
    private ParentHomeworkService homeworkService;

    @GetMapping
    public String showHomework(@AuthenticationPrincipal User parent, Model model) {
        model.addAttribute("children", homeworkService.getChildrenHomework(parent));
        return "parent/homework";
    }

    @GetMapping("/child/{childId}")
    @ResponseBody
    public ResponseEntity<?> getChildHomework(@PathVariable Long childId) {
        return ResponseEntity.ok(homeworkService.getDetailedChildHomework(childId));
    }

    @GetMapping("/stats/{childId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChildStats(@PathVariable Long childId) {
        return ResponseEntity.ok(homeworkService.getChildHomeworkStats(childId));
    }

    @PostMapping("/{homeworkId}/reminder")
    @ResponseBody
    public ResponseEntity<?> sendReminder(@PathVariable Long homeworkId) {
        try {
            homeworkService.sendHomeworkReminder(homeworkId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
```

<boltAction type="file" filePath="src/main/java/com/digital/school/controller/parent/ParentScheduleController.java">
```java
package com.digital.school.controller.parent;

import Autowired;
import ResponseEntity;
import AuthenticationPrincipal;
import Controller;
import Model;
import org.springframework.web.bind.annotation.*;
import User;
import com.digital.school.service.ParentScheduleService;

import java.time.LocalDateTime;
import Map;

@Controller
@RequestMapping("/parent/schedule")
public class ParentScheduleController {

    @Autowired
    private ParentScheduleService scheduleService;

    @GetMapping
    public String showSchedule(@AuthenticationPrincipal User parent, Model model) {
        model.addAttribute("children", scheduleService.getChildrenSchedule(parent));
        return "parent/schedule";
    }

    @GetMapping("/child/{childId}")
    @ResponseBody
    public ResponseEntity<?> getChildSchedule(
            @PathVariable Long childId,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(scheduleService.getChildSchedule(childId, start, end));
    }

    @GetMapping("/events/{childId}")
    @ResponseBody
    public ResponseEntity<?> getChildEvents(@PathVariable Long childId) {
        return ResponseEntity.ok(scheduleService.getChildEvents(childId));
    }

    @GetMapping("/stats/{childId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChildStats(@PathVariable Long childId) {
        return ResponseEntity.ok(scheduleService.getChildScheduleStats(childId));
    }
}
```

<boltAction type="file" filePath="src/main/java/com/digital/school/service/ParentHomeworkService.java">
```java
package com.digital.school.service;

import User;
import java.util.List;
import Map;

public interface ParentHomeworkService {
    List<Map<String, Object>> getChildrenHomework(User parent);
    Map<String, Object> getDetailedChildHomework(Long childId);
    Map<String, Object> getChildHomeworkStats(Long childId);
    void sendHomeworkReminder(Long homeworkId);
    boolean canAccessHomework(Long homeworkId, User parent);
}
```

<boltAction type="file" filePath="src/main/java/com/digital/school/service/ParentScheduleService.java">
```java
package com.digital.school.service;

import User;
import java.time.LocalDateTime;
import java.util.List;
import Map;

public interface ParentScheduleService {
    List<Map<String, Object>> getChildrenSchedule(User parent);
    List<Map<String, Object>> getChildSchedule(Long childId, LocalDateTime start, LocalDateTime end);
    List<Map<String, Object>> getChildEvents(Long childId);
    Map<String, Object> getChildScheduleStats(Long childId);
}
```

<boltAction type="file" filePath="src/main/java/com/digital/school/service/impl/ParentHomeworkServiceImpl.java">
```java
package com.digital.school.service.impl;

import Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.*;
import com.digital.school.repository.*;
import com.digital.school.service.ParentHomeworkService;
import com.digital.school.service.EmailService;
import com.digital.school.service.SMSService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ParentHomeworkServiceImpl implements ParentHomeworkService {

    @Autowired
    private ParentStudentRepository parentStudentRepository;
    
    @Autowired
    private StudentHomeworkRepository homeworkRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SMSService smsService;

    @Override
    public List<Map<String, Object>> getChildrenHomework(User parent) {
        return parentStudentRepository.findByParent(parent).stream()
            .map(association -> {
                User child = association.getStudent();
                Map<String, Object> childHomework = new HashMap<>();
                childHomework.put("childId", child.getId());
                childHomework.put("childName", child.getFirstName() + " " + child.getLastName());
                childHomework.put("class", child.getClasse().getName());
                childHomework.put("homework", getHomeworkDetails(child));
                childHomework.put("stats", getChildHomeworkStats(child.getId()));
                return childHomework;
            })
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getDetailedChildHomework(Long childId) {
        User child = parentStudentRepository.findByStudentId(childId)
            .map(ParentStudent::getStudent)
            .orElseThrow(() -> new RuntimeException("Enfant non trouvé"));
            
        Map<String, Object> details = new HashMap<>();
        details.put("student", child);
        details.put("homework", getHomeworkDetails(child));
        details.put("stats", getChildHomeworkStats(childId));
        details.put("subjects", getSubjectBreakdown(child));
        
        return details;
    }

    @Override
    public Map<String, Object> getChildHomeworkStats(Long childId) {
        User child = parentStudentRepository.findByStudentId(childId)
            .map(ParentStudent::getStudent)
            .orElseThrow(() -> new RuntimeException("Enfant non trouvé"));
            
        Map<String, Object> stats = new HashMap<>();
        
        // Statistiques générales
        stats.put("totalHomework", homeworkRepository.countByStudent(child));
        stats.put("completedHomework", homeworkRepository.countCompletedHomework(child));
        stats.put("pendingHomework", homeworkRepository.countPendingHomework(child));
        stats.put("lateHomework", homeworkRepository.countLateHomework(child));
        
        // Taux de complétion
        long total = homeworkRepository.countByStudent(child);
        long completed = homeworkRepository.countCompletedHomework(child);
        double completionRate = total > 0 ? (double) completed / total * 100 : 0;
        stats.put("completionRate", completionRate);
        
        // Répartition par matière
        stats.put("subjectBreakdown", getSubjectBreakdown(child));
        
        // Tendance mensuelle
        stats.put("monthlyTrend", getMonthlyTrend(child));
        
        return stats;
    }

    @Override
    @Transactional
    public void sendHomeworkReminder(Long homeworkId) {
        StudentHomework homework = homeworkRepository.findById(homeworkId)
            .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));
            
        User student = homework.getStudent();
        User parent = student.getParent();
        
        // Envoyer un email
        Map<String, Object> emailVars = new HashMap<>();
        emailVars.put("studentName", student.getFirstName());
        emailVars.put("homeworkTitle", homework.getTitle());
        emailVars.put("dueDate", homework.getDueDate());
        emailVars.put("subject", homework.getSubject().getName());
        
        emailService.sendEmail(
            student.getEmail(),
            "Rappel de devoir",
            "homework-reminder",
            emailVars
        );
        
        // Envoyer un SMS si numéro disponible
        if (student.getPhone() != null) {
            String message = String.format(
                "Rappel: Le devoir de %s (%s) est à rendre pour le %s",
                homework.getSubject().getName(),
                homework.getTitle(),
                homework.getDueDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM"))
            );
            smsService.sendSMS(student.getPhone(), message);
        }
    }

    @Override
    public boolean canAccessHomework(Long homeworkId, User parent) {
        return homeworkRepository.findById(homeworkId)
            .map(homework -> parentStudentRepository.existsByParentAndStudent(parent, homework.getStudent()))
            .orElse(false);
    }

    private List<Map<String, Object>> getHomeworkDetails(User child) {
        return homeworkRepository.findByStudent(child).stream()
            .map(homework -> {
                Map<String, Object> details = new HashMap<>();
                details.put("id", homework.getId());
                details.put("title", homework.getTitle());
                details.put("subject", homework.getSubject().getName());
                details.put("dueDate", homework.getDueDate());
                details.put("status", homework.getStatus());
                details.put("grade", homework.getGrade());
                details.put("feedback", homework.getFeedback());
                return details;
            })
            .collect(Collectors.toList());
    }

    private Map<String, Object> getSubjectBreakdown(User child) {
        return homeworkRepository.findByStudent(child).stream()
            .collect(Collectors.groupingBy(
                homework -> homework.getSubject().getName(),
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    homeworkList -> {
                        Map<String, Object> stats = new HashMap<>();
                        stats.put("total", homeworkList.size());
                        stats.put("completed", homeworkList.stream()
                            .filter(h -> "COMPLETED".equals(h.getStatus()))
                            .count());
                        stats.put("pending", homeworkList.stream()
                            .filter(h -> "PENDING".equals(h.getStatus()))
                            .count());
                        return stats;
                    }
                )
            ));
    }

    private Map<String, Long> getMonthlyTrend(User child) {
        LocalDateTime startOfYear = LocalDateTime.now()
            .withMonth(9)  // Septembre
            .withDayOfMonth(1)
            .withHour(0)
            .withMinute(0);
            
        return homeworkRepository.findByStudentAndDueDateAfter(child, startOfYear).stream()
            .collect(Collectors.groupingBy(
                homework -> homework.getDueDate().getMonth().toString(),
                Collectors.counting()
            ));
    }
}
```

<boltAction type="file" filePath="src/main/java/com/digital/school/service/impl/ParentScheduleServiceImpl.java">
```java
package com.digital.school.service.impl;

import Autowired;
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
                User child = association.getStudent();
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
        User child = parentStudentRepository.findByStudentId(childId)
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
                event.put("start", exam.getExamDate());
                event.put("end", exam.getExamDate().plusMinutes(exam.getDuration()));
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
        User child = parentStudentRepository.findByStudentId(childId)
            .map(ParentStudent::getStudent)
            .orElseThrow(() -> new RuntimeException("Enfant non trouvé"));
            
        List<Map<String, Object>> events = new ArrayList<>();
        
        // Ajouter les examens à venir
        examRepository.findUpcomingExams(child.getClasse()).forEach(exam -> {
            Map<String, Object> event = new HashMap<>();
            event.put("type", "EXAM");
            event.put("title", "Examen - " + exam.getSubject().getName());
            event.put("date", exam.getExamDate());
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
        User child = parentStudentRepository.findByStudentId(childId)
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

    private List<Map<String, Object>> getTodayClasses(User child) {
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

    private List<Map<String, Object>> getUpcomingEvents(User child) {
        List<Map<String, Object>> events = new ArrayList<>();
        
        // Examens à venir
        examRepository.findUpcomingExams(child.getClasse())
            .stream()
            .limit(5)
            .forEach(exam -> {
                Map<String, Object> event = new HashMap<>();
                event.put("type", "EXAM");
                event.put("title", exam.getName());
                event.put("date", exam.getExamDate());
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

    private double calculateWeeklyHours(User child) {
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

    private Map<String, Double> calculateSubjectDistribution(User child) {
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
```

</boltArtifact>

Je viens d'ajouter les services et contrôleurs pour :

1. Le suivi des devoirs :
   - Vue d'ensemble des devoirs par enfant
   - Statistiques de complétion
   - Rappels et notifications
   - Répartition par matière

2. L'emploi du temps :
   - Vue d'ensemble des emplois du temps
   - Événements à venir
   - Statistiques d'heures de cours
   - Répartition par matière

Voulez-vous que je continue avec les templates et les fichiers JavaScript cor