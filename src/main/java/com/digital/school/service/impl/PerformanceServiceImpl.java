package com.digital.school.service.impl;

import com.digital.school.model.Performance;
import com.digital.school.model.Student;
import com.digital.school.repository.PerformanceRepository;
import com.digital.school.repository.StudentSubmissionRepository;
import com.digital.school.repository.StudentRepository;
import com.digital.school.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PerformanceServiceImpl implements PerformanceService {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private StudentSubmissionRepository studentSubmissionRepository;

    @Autowired
    private StudentRepository studentRepository;


    @Transactional
    @Override
    public void updateStudentPerformance(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        Double newAverage = studentSubmissionRepository.calculateAverageGrade(student);

        Performance performance = performanceRepository.findByStudentId(studentId)
                .orElseGet(() -> new Performance(student));

        performance.setAverageGrade(newAverage != null ? newAverage : 0.0);
        performanceRepository.save(performance);
    }

    @Transactional
    @Override
    public void finalizeClassPerformance(Long subjectId, String title, Long classId) {

        Long totalStudents = studentSubmissionRepository.countStudentsInClass(classId);
        Long gradedStudents = studentSubmissionRepository.countGradesForClass(subjectId, title, classId);

        //commentaire
        if (gradedStudents.equals(totalStudents)) { // Vérifier si toutes les notes ont été saisies




            Double classAverage = studentSubmissionRepository.calculateClassAverage(subjectId, title, classId);
            Double successRate = studentSubmissionRepository.calculateSuccessRate(subjectId, title, classId);

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

