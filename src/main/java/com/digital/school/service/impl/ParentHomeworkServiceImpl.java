package com.digital.school.service.impl;

import com.digital.school.model.Parent;
import com.digital.school.model.ParentStudent;
import com.digital.school.model.Student;
import com.digital.school.model.StudentHomework;
import com.digital.school.model.enumerated.StudentSubmissionStatus;
import com.digital.school.repository.ParentHomeworkRepository;
import com.digital.school.repository.ParentStudentRepository;
import com.digital.school.repository.StudentHomeworkRepository;
import com.digital.school.service.EmailService;
import com.digital.school.service.ParentHomeworkService;
import com.digital.school.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ParentHomeworkServiceImpl implements ParentHomeworkService {

    @Autowired
    private ParentStudentRepository parentStudentRepository;

    @Autowired
    private StudentHomeworkRepository studentHomeworkRepository;

    @Autowired
    private ParentHomeworkRepository homeworkRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SMSService smsService;

    @Override
    public List<Map<String, Object>> getChildrenHomework(Parent parent) {
        return parentStudentRepository.findByParent(parent).stream()
                .map(association -> {
                    Student child = association.getStudent();
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
        Student child = parentStudentRepository.findByStudentId(childId)
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
        stats.put("totalHomework", studentHomeworkRepository.countByStudent(child));
        stats.put("completedHomework", studentHomeworkRepository.countCompletedHomework(child));
        stats.put("pendingHomework", studentHomeworkRepository.countPendingHomework(child));
        stats.put("lateHomework", studentHomeworkRepository.countLateHomework(child));

        long total = studentHomeworkRepository.countByStudent(child);
        long completed = studentHomeworkRepository.countCompletedHomework(child);
        double completionRate = total > 0 ? (double) completed / total * 100 : 0;
        stats.put("completionRate", completionRate);

        stats.put("subjectBreakdown", getSubjectBreakdown(child));
        //stats.put("monthlyTrend", getMonthlyTrend(child));
        return stats;
    }

    @Override
    @Transactional
    public void sendHomeworkReminder(Long homeworkId) {
        StudentHomework studentHomework = studentHomeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));

        Student student = studentHomework.getStudent();
        Parent parent = student.getParent();

        Map<String, Object> emailVars = new HashMap<>();
        emailVars.put("studentName", student.getFirstName());
        emailVars.put("homeworkTitle", studentHomework.getHomework().getTitle());
        emailVars.put("dueDate", studentHomework.getHomework().getDueDate());
        emailVars.put("subject", studentHomework.getHomework().getSubject().getName());

        emailService.sendEmail(
                student.getEmail(),
                "Rappel de devoir",
                "homework-reminder",
                emailVars
        );

        if (student.getPhone() != null) {
            String message = String.format(
                    "Rappel: Le devoir de %s (%s) est à rendre pour le %s",
                    studentHomework.getHomework().getSubject().getName(),
                    studentHomework.getHomework().getTitle(),
                    studentHomework.getHomework().getDueDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM"))
            );
            smsService.sendSMS(student.getPhone(), message);
        }
    }

    @Override
    public boolean canAccessHomework(Long homeworkId, Parent parent) {
        return studentHomeworkRepository.findById(homeworkId)
                .map(hw -> parentStudentRepository.existsByParentAndStudent(parent, hw.getStudent()))
                .orElse(false);
    }

    private List<Map<String, Object>> getHomeworkDetails(Student child) {
        return studentHomeworkRepository.findByStudent(child).stream()
                .map(studentHomework -> {
                    Map<String, Object> details = new HashMap<>();
                    details.put("id", studentHomework.getId());
                    details.put("title", studentHomework.getHomework().getTitle());
                    details.put("subject", studentHomework.getHomework().getSubject().getName());
                    details.put("dueDate", studentHomework.getHomework().getDueDate());
                    details.put("status", studentHomework.getStatus());
                    // Les informations de note et feedback sont gérées dans la soumission
                    details.put("grade", studentHomework.getGradeType());
                    details.put("feedback", studentHomework.getComments());
                    return details;
                })
                .collect(Collectors.toList());
    }

    private Map<String, Object> getSubjectBreakdown(Student child) {
        return studentHomeworkRepository.findByStudent(child).stream()
                .collect(Collectors.groupingBy(
                        sh -> sh.getHomework().getSubject().getName(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                homeworkList -> {
                                    Map<String, Object> stats = new HashMap<>();
                                    stats.put("total", homeworkList.size());
                                    stats.put("completed", homeworkList.stream()
                                            .filter(h -> StudentSubmissionStatus.COMPLETED.equals(h.getStatus()))
                                            .count());
                                    stats.put("pending", homeworkList.stream()
                                            .filter(h -> StudentSubmissionStatus.PENDING.equals(h.getStatus()))
                                            .count());
                                    return stats;
                                }
                        )
                ));
    }
/* a implementer
    private Map<String, Long> getMonthlyTrend(Student child) {
        LocalDateTime startOfYear = LocalDateTime.now()
                .withMonth(9)
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        // Ici, nous utilisons une méthode du ParentStudentRepository pour récupérer les évaluations
        // (Homework) associées à un élève dont la date d'échéance est après startOfYear.
        // Assurez-vous que cette méthode est correctement implémentée.
        return parentStudentRepository.findByStudentAndDueDateAfter(child, startOfYear).stream()
                .collect(Collectors.groupingBy(
                        hw -> hw.getDueDate().getMonth().toString(),
                        Collectors.counting()
                ));
    }

 */
}
