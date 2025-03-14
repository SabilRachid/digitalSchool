package com.digital.school.service.impl;

import com.digital.school.model.*;
import com.digital.school.model.enumerated.EvaluationStatus;
import com.digital.school.repository.*;
import com.digital.school.service.EmailService;
import com.digital.school.service.ParentDashboardService;
import com.digital.school.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Transactional(readOnly = true)
public class ParentHomeworkServiceImpl implements ParentDashboardService {

    @Autowired
    private ParentStudentRepository parentStudentRepository;

    @Autowired
    private StudentAttendanceRepository studentAttendanceRepository;

    @Autowired
    private EventRepository eventRepository;

    // Remplacement de studentHomeworkRepository par homeworkRepository (ParentHomeworkRepository)
    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private EvaluationGradeRepository evaluationGradeRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SMSService smsService;

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
            long unjustifiedAbsences = studentAttendanceRepository.countUnjustifiedAbsences(child);
            if (unjustifiedAbsences > 0) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("type", "ABSENCE");
                alert.put("severity", "WARNING");
                alert.put("message", String.format("%s a %d absence(s) non justifiée(s)",
                        child.getFirstName(), unjustifiedAbsences));
                alert.put("childId", child.getId());
                alerts.add(alert);
            }
        }

        // Vérifier les notes faibles via EvaluationGradeRepository
        for (Student child : children) {
            List<EvaluationGrade> lowGrades = evaluationGradeRepository.findLowGrades(child);
            if (!lowGrades.isEmpty()) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("type", "GRADE");
                alert.put("severity", "WARNING");
                alert.put("message", String.format("%s a obtenu une note inférieure à 10 en %s",
                        child.getFirstName(), lowGrades.get(0).getEvaluation().getSubject().getName()));
                alert.put("childId", child.getId());
                alerts.add(alert);
            }
        }

        // Vérifier les devoirs en retard via homeworkRepository
        for (Student child : children) {
            List<EvaluationGrade> lateHomework = evaluationGradeRepository.findLateHomework(child);
            if (!lateHomework.isEmpty()) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("type", "HOMEWORK");
                alert.put("severity", "WARNING");
                alert.put("message", String.format("%s a %d devoir(s) en retard",
                        child.getFirstName(), lateHomework.size()));
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

        stats.put("totalChildren", children.size());
        stats.put("averageAttendance",
                children.stream().mapToDouble(this::calculateAttendanceRate).average().orElse(0.0));
        stats.put("averageGrade",
                children.stream().mapToDouble(this::calculateAverageGrade).average().orElse(0.0));

        stats.put("childrenStats", children.stream().map(child -> {
            Map<String, Object> childStats = new HashMap<>();
            childStats.put("id", child.getId());
            childStats.put("name", child.getFirstName());
            childStats.put("averageGrade", calculateAverageGrade(child));
            childStats.put("attendanceRate", calculateAttendanceRate(child));
            childStats.put("rank", calculateRank(child));
            return childStats;
        }).collect(Collectors.toList()));

        return stats;
    }


    private int calculateRank(Student student) {
        Integer rank = evaluationGradeRepository.calculateStudentRank(student.getId(), student.getClasse().getId());
        return rank != null ? rank : 0;
    }


    @Override
    public List<Map<String, Object>> getUpcomingEvents(Parent parent) {
        List<Map<String, Object>> events = new ArrayList<>();

        List<Student> children = parentStudentRepository.findByParent(parent).stream()
                .map(ParentStudent::getStudent)
                .collect(Collectors.toList());

        for (Student child : children) {
            eventRepository.findUpcomingExams(child).forEach(exam -> {
                Map<String, Object> event = new HashMap<>();
                event.put("type", "EXAM");
                event.put("title", exam.getTitle());
                event.put("date", exam.getStartTime());
                event.put("childName", child.getFirstName());
                event.put("subject", exam.getSubject().getName());
                events.add(event);
            });

            homeworkRepository.findByStudentAndStatusOrderByDueDateDesc(child, EvaluationStatus.UPCOMING)
                    .forEach(homework -> {
                        Map<String, Object> event = new HashMap<>();
                        event.put("type", "HOMEWORK");
                        event.put("title", homework.getTitle());
                        LocalDateTime dueDateTime = homework.getDueDate().atStartOfDay();
                        event.put("date", dueDateTime);
                        event.put("childName", child.getFirstName());
                        event.put("subject", homework.getSubject().getName());
                        events.add(event);
                    });
        }

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
       // performance.put("progression", calculateProgression(child));
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

        return evaluationGradeRepository.findByStudentOrderByGradedAtDesc(child).stream()
                .map(grade -> {
                    Map<String, Object> gradeMap = new HashMap<>();
                    gradeMap.put("subject", grade.getEvaluation().getSubject().getName());
                    gradeMap.put("value", grade.getGrade());
                    gradeMap.put("title", grade.getEvaluation().getTitle());
                    gradeMap.put("date", grade.getGradedAt());
                    return gradeMap;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getChildHomework(Long childId) {
        Student child = parentStudentRepository.findByStudentId(childId)
                .map(ParentStudent::getStudent)
                .orElseThrow(() -> new RuntimeException("Enfant non trouvé"));

        List<Homework> homeworks = homeworkRepository.findByStudent(child);

        return homeworks.stream().map(hw -> {
                    Map<String, Object> homeworkMap = new HashMap<>();
                    homeworkMap.put("subject", hw.getSubject().getName());
                    homeworkMap.put("title", hw.getTitle());
                    homeworkMap.put("dueDate", hw.getDueDate());
                    homeworkMap.put("status", hw.getStatus());
                    return homeworkMap;
                })
                .collect(Collectors.toList());
    }

    // Méthodes utilitaires

    private double calculateAverageGrade(Student student) {
        Double avg = evaluationGradeRepository.calculateAverageGrade(student);
        return avg != null ? avg : 0.0;
    }

    private double calculateAttendanceRate(Student student) {
        return studentAttendanceRepository.calculateAttendanceRate(student);
    }

    private int countPendingHomework(Student student) {
        return homeworkRepository.countPendingHomework(student);
    }

    private int countUpcomingExams(Student student) {
        return eventRepository.countUpcomingExams(student);
    }

    private Map<String, List<Double>> calculateProgression(Student student) {
        Map<String, List<Double>> progression = new HashMap<>();
        List<Object[]> results = evaluationGradeRepository.findGradesByMonth(student.getId());
        for (Object[] row : results) {
            String month = (String) row[0];
            Double grade = ((Number) row[1]).doubleValue();
            progression.computeIfAbsent(month, k -> new ArrayList<>()).add(grade);
        }
        return progression;
    }


    private Map<String, Double> calculateSubjectAverages(Student student) {
        List<Object[]> results = evaluationGradeRepository.findSubjectAveragesByStudent(student);
        Map<String, Double> averages = new HashMap<>();
        for (Object[] row : results) {
            String subject = (String) row[0];
            Double average = (Double) row[1];
            averages.put(subject, average);
        }
        return averages;
    }


    private List<Map<String, Object>> getAbsenceDetails(Student student) {
        return studentAttendanceRepository.findAbsenceDetails(student);
    }

    private List<Map<String, Object>> getLateDetails(Student student) {
        return studentAttendanceRepository.findLateDetails(student);
    }
}
