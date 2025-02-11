package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.*;
import com.digital.school.repository.*;
import com.digital.school.service.ParentDashboardService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ParentDashboardServiceImpl implements ParentDashboardService {

    @Autowired
    private ParentStudentRepository parentStudentRepository;
    
    @Autowired
    private StudentGradeRepository gradeRepository;
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private StudentHomeworkRepository homeworkRepository;

    @Autowired
    private EventRepository eventRepository;

    @Override
    public List<Map<String, Object>> getChildrenOverview(Parent parent) {
        return parentStudentRepository.findByParent(parent).stream()
            .map(association -> {
                Student child = association.getStudent();
                Map<String, Object> overview = new HashMap<>();
                overview.put("id", child.getId());
                overview.put("name", child.getFirstName() + " " + child.getLastName());
                overview.put("class", child.getClasse().getName());
                overview.put("averageGrade", calculateAverageGrade(child));
                overview.put("attendanceRate", calculateAttendanceRate(child));
                overview.put("pendingHomework", countPendingHomework(child));
                overview.put("upcomingExams", countUpcomingExams(child));
                return overview;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getParentAlerts(Parent parent) {
        List<Map<String, Object>> alerts = new ArrayList<>();
        
        // Récupérer les enfants du parent
        List<Student> children = parentStudentRepository.findByParent(parent).stream()
            .map(ParentStudent::getStudent)
            .collect(Collectors.toList());
            
        // Vérifier les absences non justifiées
        for (Student child : children) {
            long unjustifiedAbsences = attendanceRepository.countUnjustifiedAbsences(child);
            if (unjustifiedAbsences > 0) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("type", "ABSENCE");
                alert.put("severity", "WARNING");
                alert.put("message", String.format(
                    "%s a %d absence(s) non justifiée(s)",
                    child.getFirstName(),
                    unjustifiedAbsences
                ));
                alert.put("childId", child.getId());
                alerts.add(alert);
            }
        }
        
        // Vérifier les notes faibles
        for (Student child : children) {
            List<StudentGrade> lowGrades = gradeRepository.findLowGrades(child);
            if (!lowGrades.isEmpty()) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("type", "GRADE");
                alert.put("severity", "WARNING");
                alert.put("message", String.format(
                    "%s a obtenu une note inférieure à 10 en %s",
                    child.getFirstName(),
                    lowGrades.get(0).getSubject().getName()
                ));
                alert.put("childId", child.getId());
                alerts.add(alert);
            }
        }
        
        // Vérifier les devoirs en retard
        for (Student child : children) {
            List<StudentHomework> lateHomework = homeworkRepository.findLateHomework(child);
            if (!lateHomework.isEmpty()) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("type", "HOMEWORK");
                alert.put("severity", "WARNING");
                alert.put("message", String.format(
                    "%s a %d devoir(s) en retard",
                    child.getFirstName(),
                    lateHomework.size()
                ));
                alert.put("childId", child.getId());
                alerts.add(alert);
            }
        }
        
        return alerts;
    }

    @Override
    public Map<String, Object> getParentStats(Parent parent) {
        Map<String, Object> stats = new HashMap<>();
        
        List<Student> children = parentStudentRepository.findByParent(parent).stream()
            .map(ParentStudent::getStudent)
            .collect(Collectors.toList());
            
        // Statistiques globales
        stats.put("totalChildren", children.size());
        stats.put("averageAttendance", 
            children.stream()
                .mapToDouble(this::calculateAttendanceRate)
                .average()
                .orElse(0.0));
        stats.put("averageGrade",
            children.stream()
                .mapToDouble(this::calculateAverageGrade)
                .average()
                .orElse(0.0));
                
        // Statistiques par enfant
        stats.put("childrenStats", children.stream()
            .map(child -> {
                Map<String, Object> childStats = new HashMap<>();
                childStats.put("id", child.getId());
                childStats.put("name", child.getFirstName());
                childStats.put("averageGrade", calculateAverageGrade(child));
                childStats.put("attendanceRate", calculateAttendanceRate(child));
                childStats.put("rank", calculateRank(child));
                return childStats;
            })
            .collect(Collectors.toList()));
            
        return stats;
    }

    @Override
    public List<Map<String, Object>> getUpcomingEvents(Parent parent) {
        List<Map<String, Object>> events = new ArrayList<>();
        
        // Récupérer les enfants du parent
        List<Student> children = parentStudentRepository.findByParent(parent).stream()
            .map(ParentStudent::getStudent)
            .collect(Collectors.toList());
            
        // Récupérer les événements pour chaque enfant
        for (Student child : children) {
            // Examens à venir
            eventRepository.findUpcomingExams(child).forEach(exam -> {
                Map<String, Object> event = new HashMap<>();
                event.put("type", "EXAM");
                event.put("title", exam.getTitle());
                event.put("date", exam.getStartTime());
                event.put("childName", child.getFirstName());
                event.put("subject", exam.getSubject().getName());
                events.add(event);
            });
            
            // Devoirs à rendre
            homeworkRepository.findPendingHomework(child).forEach(homework -> {
                Map<String, Object> event = new HashMap<>();
                event.put("type", "HOMEWORK");
                event.put("title", homework.getTitle());
                event.put("date", homework.getDueDate());
                event.put("childName", child.getFirstName());
                event.put("subject", homework.getSubject().getName());
                events.add(event);
            });
        }
        
        // Trier par date
        events.sort((e1, e2) -> ((LocalDateTime) e1.get("date"))
            .compareTo((LocalDateTime) e2.get("date")));
            
        return events;
    }

    @Override
    public Map<String, Object> getChildPerformance(Long childId) {
        Student child = parentStudentRepository.findByStudentId(childId)
            .map(ParentStudent::getStudent)
            .orElseThrow(() -> new RuntimeException("Enfant non trouvé"));
            
        Map<String, Object> performance = new HashMap<>();
        performance.put("averageGrade", calculateAverageGrade(child));
        performance.put("rank", calculateRank(child));
        performance.put("subjectAverages", calculateSubjectAverages(child));
        performance.put("progression", calculateProgression(child));
        
        return performance;
    }

    @Override
    public Map<String, Object> getChildAttendance(Long childId) {
        Student child = parentStudentRepository.findByStudentId(childId)
            .map(ParentStudent::getStudent)
            .orElseThrow(() -> new RuntimeException("Enfant non trouvé"));
            
        Map<String, Object> attendance = new HashMap<>();
        attendance.put("attendanceRate", calculateAttendanceRate(child));
        attendance.put("absences", getAbsenceDetails(child));
        attendance.put("lates", getLateDetails(child));
        
        return attendance;
    }

    @Override
    public List<Map<String, Object>> getChildGrades(Long childId) {
        Student child = parentStudentRepository.findByStudentId(childId)
            .map(ParentStudent::getStudent)
            .orElseThrow(() -> new RuntimeException("Enfant non trouvé"));
            
        return gradeRepository.findByStudentOrderByDateDesc(child).stream()
            .map(grade -> {
                Map<String, Object> gradeMap = new HashMap<>();
                gradeMap.put("subject", grade.getSubject().getName());
                gradeMap.put("value", grade.getValue());
                gradeMap.put("title", grade.getTitle());
                gradeMap.put("date", grade.getDate());
                gradeMap.put("classAverage", grade.getClassAverage());
                return gradeMap;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getChildHomework(Long childId) {
        Student child = parentStudentRepository.findByStudentId(childId)
            .map(ParentStudent::getStudent)
            .orElseThrow(() -> new RuntimeException("Enfant non trouvé"));


        List<StudentHomework> homeworks = homeworkRepository.findByStudent(child);

        return homeworks.stream().map(homework  -> {
                Map<String, Object> homeworkMap = new HashMap<>();
                homeworkMap.put("subject", homework.getSubject().getName());
                homeworkMap.put("title", homework.getTitle());
                homeworkMap.put("dueDate", homework.getDueDate());
                homeworkMap.put("status", homework.getStatus());
                homeworkMap.put("grade", homework.getGrade());
                return homeworkMap;
            })
            .collect(Collectors.toList());
    }

    // Méthodes utilitaires
    private double calculateAverageGrade(Student student) {
        return gradeRepository.calculateAverageGrade(Optional.ofNullable(student));
    }

    private double calculateAttendanceRate(Student student) {
        return attendanceRepository.calculateAttendanceRate(student);
    }

    private int countPendingHomework(Student student) {
        return homeworkRepository.countPendingHomework(student);
    }

    private int countUpcomingExams(Student student) {
        return eventRepository.countUpcomingExams(student);
    }

    private int calculateRank(Student student) {
        return gradeRepository.calculateStudentRank(
            student.getId(), 
            student.getClasse().getId()
        );
    }

    private Map<String, Double> calculateSubjectAverages(Student student) {
        return gradeRepository.calculateSubjectAverages(student);
    }

    private Map<String, List<Double>> calculateProgression(Student student) {
        return gradeRepository.calculateProgression(student);
    }

    private List<Map<String, Object>> getAbsenceDetails(Student student) {
        return attendanceRepository.findAbsenceDetails(student);
    }

    private List<Map<String, Object>> getLateDetails(Student student) {
        return attendanceRepository.findLateDetails(student);
    }
}
