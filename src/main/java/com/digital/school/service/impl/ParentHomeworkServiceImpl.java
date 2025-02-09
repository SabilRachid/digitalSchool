package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
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
        Student child = parentStudentRepository.findByStudentId(childId)
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

        Student student = homework.getStudent();
        Parent parent = student.getParent();

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
    public boolean canAccessHomework(Long homeworkId, Parent parent) {
        return homeworkRepository.findById(homeworkId)
                .map(homework -> parentStudentRepository.existsByParentAndStudent(parent, homework.getStudent()))
                .orElse(false);
    }

    private List<Map<String, Object>> getHomeworkDetails(Student child) {
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
