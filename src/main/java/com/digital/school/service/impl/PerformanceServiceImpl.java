package com.digital.school.service.impl;

import com.digital.school.model.Performance;
import com.digital.school.model.Student;
import com.digital.school.repository.PerformanceRepository;
import com.digital.school.repository.EvaluationGradeRepository;
import com.digital.school.repository.StudentRepository;
import com.digital.school.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PerformanceServiceImpl implements PerformanceService {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private EvaluationGradeRepository evaluationGradeRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Transactional
    @Override
    public void updateStudentPerformance(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        // Calculer la moyenne des notes pour cet étudiant via EvaluationGradeRepository
        Double newAverage = evaluationGradeRepository.calculateAverageGrade(student);

        Performance performance = performanceRepository.findByStudentId(studentId)
                .orElseGet(() -> new Performance(student));

        performance.setAverageGrade(newAverage != null ? newAverage : 0.0);
        performanceRepository.save(performance);
    }

    @Transactional
    @Override
    public void finalizeClassPerformance(Long subjectId, String title, Long classId) {
        // Calcul du nombre total d'étudiants dans la classe et le nombre d'étudiants ayant une note pour l'évaluation
        Long totalStudents = evaluationGradeRepository.countStudentsInClass(classId);
        Long gradedStudents = evaluationGradeRepository.countGradesForClass(subjectId, title, classId);

        if (gradedStudents.equals(totalStudents)) { // Toutes les notes ont été saisies
            Double classAverage = evaluationGradeRepository.calculateClassAverage(subjectId, title, classId);
            Double successRate = evaluationGradeRepository.calculateSuccessRate(subjectId, title, classId);

            List<Performance> performances = performanceRepository.findAllByClasseOrderByAverageDesc(classId);
            int rank = 1;
            for (Performance perf : performances) {
                perf.setRank(rank++);
                perf.setClassAverage(classAverage);
                performanceRepository.save(perf);
            }
        }
    }
}
