package com.digital.school.service.impl;

import com.digital.school.model.Professor;
import com.digital.school.model.enumerated.EvaluationStatus;
import com.digital.school.service.ClasseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.repository.*;
import com.digital.school.service.ProfessorDashboardService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ProfessorDashboardServiceImpl implements ProfessorDashboardService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ParentHomeworkRepository homeworkRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private StudentAttendanceRepository studentAttendanceRepository;

    @Autowired
    private ProfessorDashboardRepository professorDashboardRepository;
    @Autowired
    private ClasseService classeService;
    @Autowired
    private ClasseRepository classeRepository;

    @Override
    public Map<String, Object> getProfessorStats(Professor professor) {
        Map<String, Object> stats = new HashMap<>();

        // Calculate total students
        int totalStudents = (int) classeRepository.findByProfessor(professor).stream()
                .flatMap(classe -> classe.getStudents().stream())
                .distinct()
                .count();
        stats.put("totalStudents", totalStudents);

        // Calculate total classes
        long totalClasses = courseRepository.findByProfessor(professor).stream()
                .map(course -> course.getClasse())
                .distinct()
                .count();
        stats.put("totalClasses", totalClasses);

        // Count pending homework
        int pendingHomework = homeworkRepository.countPendingGradingByProfessor(professor, EvaluationStatus.UPCOMING);
        stats.put("pendingHomework", pendingHomework);

        // Count submissions today
        int submittedToday = homeworkRepository.countSubmissionsToday(professor);
        stats.put("submittedToday", submittedToday);

        // Count upcoming exams
        int upcomingExams = examRepository.countUpcomingExamsByProfessor(professor);
        stats.put("upcomingExams", upcomingExams);

        // Calculate average attendance
        double averageAttendance = studentAttendanceRepository.calculateAverageAttendanceForProfessor(professor);
        stats.put("averageAttendance", Math.round(averageAttendance * 100));
        return stats;
    }

    public List<Map<String, Object>> getClassPerformance() {
        List<Object[]> results = professorDashboardRepository.getClassPerformance();
        return results.stream().map(row -> Map.of("className", row[0], "average", row[1])).toList();
    }

    public List<Map<String, Object>> getGradesDistribution() {
        List<Object[]> results = professorDashboardRepository.getGradesDistribution();
        return results.stream().map(row -> Map.of("range", row[0], "count", row[1])).toList();
    }

    public Map<String, Integer> getParticipationRate() {
        List<Object[]> results = professorDashboardRepository.getParticipationRate();
        Object[] row = results.get(0);
        return Map.of(
                "high", row[0] != null ? ((Number) row[0]).intValue() : 0,
                "medium", row[1] != null ? ((Number) row[1]).intValue() : 0,
                "low", row[2] != null ? ((Number) row[2]).intValue() : 0
        );

    }

    public List<Map<String, Object>> getAverageProgression() {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(5);
        List<Object[]> results = professorDashboardRepository.getAverageProgression(startDate);
        return results.stream().map(row -> Map.of("month", row[0], "average", row[1])).toList();
    }

}