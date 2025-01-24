package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.*;
import com.digital.school.repository.*;
import com.digital.school.service.StudentDashboardService;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StudentDashboardServiceImpl implements StudentDashboardService {

    @Autowired
    private StudentGradeRepository gradeRepository;
    
    @Autowired
    private StudentHomeworkRepository homeworkRepository;
    
    @Autowired
    private LearningResourceRepository resourceRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private AttendanceRepository attendanceRepository;

    @Override
    public StudentDashboardStats getStudentStats(User student) {
        StudentDashboardStats stats = new StudentDashboardStats();
        
        // Calculer le taux de présence
        stats.setAttendanceRate(attendanceRepository.calculateAttendanceRate(student));
        
        // Calculer la moyenne générale
        Double averageGrade = gradeRepository.calculateAverageGrade(student);
        stats.setAverageGrade(averageGrade != null ? averageGrade : 0.0);
        
        // Compter les devoirs en attente
        stats.setPendingHomework(homeworkRepository.countPendingHomework(student));
        
        // Compter les examens à venir
        stats.setUpcomingExams(eventRepository.countUpcomingExams(student));
        
        return stats;
    }

    @Override
    public List<StudentGrade> getRecentGrades(User student) {
        return gradeRepository.findRecentGrades(student);
    }

    @Override
    public List<StudentHomework> getPendingHomework(User student) {
        return homeworkRepository.findByStudentAndStatusOrderByDueDateAsc(student, "PENDING");
    }

    @Override
    public List<Map<String, Object>> getSubjectsWithResources(User student) {
        return student.getClasse().getSubjects().stream()
            .map(subject -> {
                Map<String, Object> map = new HashMap<>();
                map.put("subject", subject);
                map.put("recentResources", resourceRepository.findRecentResources(subject));
                map.put("resourceCount", resourceRepository.countBySubject(subject));
                return map;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<Event> getUpcomingEvents(User student) {
        return eventRepository.findUpcomingEventsByStudent(student);
    }
}
