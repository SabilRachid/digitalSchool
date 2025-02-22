package com.digital.school.service.impl;

import com.digital.school.model.Parent;
import com.digital.school.model.ParentStudent;
import com.digital.school.model.Student;
import com.digital.school.model.StudentHomework;
import com.digital.school.model.StudentSubmission;
import com.digital.school.model.enumerated.StudentSubmissionStatus;
import com.digital.school.repository.AttendanceRepository;
import com.digital.school.repository.EventRepository;
import com.digital.school.repository.ParentHomeworkRepository;
import com.digital.school.repository.ParentStudentRepository;
import com.digital.school.repository.StudentSubmissionRepository;
import com.digital.school.repository.StudentHomeworkRepository;
import com.digital.school.repository.StudentSubmissionRepository;
import com.digital.school.service.EmailService;
import com.digital.school.service.ParentDashboardService;
import com.digital.school.service.ParentHomeworkService;
import com.digital.school.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ParentDashboardServiceImpl implements ParentDashboardService {

    @Autowired
    private ParentStudentRepository parentStudentRepository;

    @Autowired
    private StudentSubmissionRepository studentSubmissionRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentHomeworkRepository studentHomeworkRepository;

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
            List<StudentSubmission> lowGrades = studentSubmissionRepository.findLowGrades(child);
            if (!lowGrades.isEmpty()) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("type", "GRADE");
                alert.put("severity", "WARNING");
                alert.put("message", String.format(
                        "%s a obtenu une note inférieure à 10 en %s",
                        child.getFirstName(),
                        lowGrades.get(0).getEvaluation().getSubject().getName()
                ));
                alert.put("childId", child.getId());
                alerts.add(alert);
            }
        }

        // Vérifier les devoirs en retard
        for (Student child : children) {
            // On utilise ici la méthode findByStudentAndStatusOrderByDueDateDesc pour obtenir les devoirs en attente
            List<StudentHomework> lateHomework = studentHomeworkRepository
                    .findByStudentAndStatusOrderByDueDateDesc(child, StudentSubmissionStatus.LATE);
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

        for (Student child : children) {
            // Examens à venir
            eventRepository.findUpcomingExams(child).forEach(exam -> {
                Map<String, Object> event = new HashMap<>();
                event.put("type", "EXAM");
                event.put("title", exam.getTitle());
                event.put("date", exam.getStartTime());  // exam.getStartTime() est supposé être un LocalDateTime
                event.put("childName", child.getFirstName());
                event.put("subject", exam.getSubject().getName());
                events.add(event);
            });

            // Devoirs à rendre (pending homework)
            // Utilisation de la méthode findByStudentAndStatusOrderByDueDateDesc pour les devoirs dont le statut est PENDING
            studentHomeworkRepository.findByStudentAndStatusOrderByDueDateDesc(child, StudentSubmissionStatus.PENDING)
                    .forEach(homework -> {
                        Map<String, Object> event = new HashMap<>();
                        event.put("type", "HOMEWORK");
                        event.put("title", homework.getHomework().getTitle());
                        // Conversion de dueDate (LocalDate) en LocalDateTime pour le tri
                        LocalDateTime dueDateTime = homework.getHomework().getDueDate().atStartOfDay();
                        event.put("date", dueDateTime);
                        event.put("childName", child.getFirstName());
                        event.put("subject", homework.getHomework().getSubject().getName());
                        events.add(event);
                    });
        }

        // Trier les événements par date
        events.sort((e1, e2) -> ((LocalDateTime) e1.get("date")).compareTo((LocalDateTime) e2.get("date")));

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

        return studentSubmissionRepository.findByStudentOrderByDateDesc(child).stream()
                .map(submission -> {
                    Map<String, Object> gradeMap = new HashMap<>();
                    gradeMap.put("subject", submission.getEvaluation().getSubject().getName());
                    gradeMap.put("value", submission.getValue());
                    gradeMap.put("title", submission.getEvaluation().getTitle());
                    gradeMap.put("date", submission.getGradedAt());
                    // Supposons ici que getClassAverage() est défini dans StudentSubmission ou calculé
                    gradeMap.put("classAverage", submission.getClassAverage());
                    return gradeMap;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getChildHomework(Long childId) {
        Student child = parentStudentRepository.findByStudentId(childId)
                .map(ParentStudent::getStudent)
                .orElseThrow(() -> new RuntimeException("Enfant non trouvé"));

        List<StudentHomework> homeworks = studentHomeworkRepository.findByStudent(child);

        return homeworks.stream().map(homework -> {
                    Map<String, Object> homeworkMap = new HashMap<>();
                    homeworkMap.put("subject", homework.getHomework().getSubject().getName());
                    homeworkMap.put("title", homework.getHomework().getTitle());
                    homeworkMap.put("dueDate", homework.getHomework().getDueDate());
                    homeworkMap.put("status", homework.getStatus());
                    homeworkMap.put("grade", homework.getGradeType());
                    return homeworkMap;
                })
                .collect(Collectors.toList());
    }

    // Méthodes utilitaires
    private double calculateAverageGrade(Student student) {
        return studentSubmissionRepository.calculateAverageGrade(student);
    }

    private double calculateAttendanceRate(Student student) {
        return attendanceRepository.calculateAttendanceRate(student);
    }

    private int countPendingHomework(Student student) {
        return studentHomeworkRepository.countPendingHomework(student);
    }

    private int countUpcomingExams(Student student) {
        return eventRepository.countUpcomingExams(student);
    }

    private int calculateRank(Student student) {
        Integer rank = studentSubmissionRepository.calculateStudentRank(
                student.getId(),
                student.getClasse().getId()
        );
        return rank != null ? rank : 0;
    }

    private Map<String, Double> calculateSubjectAverages(Student student) {
        return studentSubmissionRepository.calculateSubjectAverages(student);
    }

    private Map<String, List<Double>> calculateProgression(Student student) {
        return studentSubmissionRepository.calculateProgression(student);
    }

    private List<Map<String, Object>> getAbsenceDetails(Student student) {
        return attendanceRepository.findAbsenceDetails(student);
    }

    private List<Map<String, Object>> getLateDetails(Student student) {
        return attendanceRepository.findLateDetails(student);
    }
}
