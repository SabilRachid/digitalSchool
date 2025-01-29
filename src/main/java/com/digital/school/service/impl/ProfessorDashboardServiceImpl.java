package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.User;
import com.digital.school.repository.*;
import com.digital.school.service.ProfessorDashboardService;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ProfessorDashboardServiceImpl implements ProfessorDashboardService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Override
    public Map<String, Object> getProfessorStats(User professor) {
        Map<String, Object> stats = new HashMap<>();

        // Calculate total students
        int totalStudents = courseRepository.findByProfessor(professor).stream()
                .mapToInt(course -> course.getClasse().getStudents().size())
                .sum();
        stats.put("totalStudents", totalStudents);

        // Calculate total classes

        long totalClasses = courseRepository.findByProfessor(professor).stream()
                .map(course -> course.getClasse())
                .distinct()
                .count();
        stats.put("totalClasses", totalClasses);

        // Count pending homework
        int pendingHomework = homeworkRepository.countPendingGradingByProfessor(professor);
        stats.put("pendingHomework", pendingHomework);

        // Count submissions today
        int submittedToday = homeworkRepository.countSubmissionsToday(professor);
        stats.put("submittedToday", submittedToday);

        // Count upcoming exams
        int upcomingExams = examRepository.countUpcomingExamsByProfessor(professor);
        stats.put("upcomingExams", upcomingExams);

        // Calculate average attendance
        double averageAttendance = attendanceRepository.calculateAverageAttendanceForProfessor(professor);
        stats.put("averageAttendance", Math.round(averageAttendance * 100));

        return stats;
    }
}