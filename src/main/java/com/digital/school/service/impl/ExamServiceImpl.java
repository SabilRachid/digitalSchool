package com.digital.school.service.impl;

```java
package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.*;
import com.digital.school.repository.ExamRepository;
import com.digital.school.repository.ExamResultRepository;
import com.digital.school.service.ExamService;
import com.digital.school.service.EmailService;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private ExamResultRepository examResultRepository;
    
    @Autowired
    private EmailService emailService;

    @Override
    public List<Exam> findByProfessor(User professor) {
        return examRepository.findByCreatedBy(professor);
    }

    @Override
    public Optional<Exam> findById(Long id) {
        return examRepository.findById(id);
    }

    @Override
    @Transactional
    public Exam save(Exam exam) {
        exam.setCreatedAt(LocalDateTime.now());
        return examRepository.save(exam);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        examRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Exam publish(Long id) {
        Exam exam = findById(id)
            .orElseThrow(() -> new RuntimeException("Examen non trouvé"));
            
        exam.setIsPublished(true);
        exam = examRepository.save(exam);
        
        // Notifier les étudiants
        exam.getClasse().getStudents().forEach(student -> {
            Map<String, Object> variables = new HashMap<>();
            variables.put("examName", exam.getName());
            variables.put("examDate", exam.getExamDate());
            variables.put("duration", exam.getDuration());
            
            emailService.sendEmail(
                student.getEmail(),
                "Nouvel examen publié",
                "exam-notification",
                variables
            );
        });
        
        return exam;
    }

    @Override
    @Transactional
    public ExamResult publishResults(Long id, ExamResult results) {
        Exam exam = findById(id)
            .orElseThrow(() -> new RuntimeException("Examen non trouvé"));
            
        results.setExam(exam);
        results.setPublishedAt(LocalDateTime.now());
        
        ExamResult savedResults = examResultRepository.save(results);
        
        // Notifier les étudiants
        exam.getClasse().getStudents().forEach(student -> {
            Map<String, Object> variables = new HashMap<>();
            variables.put("examName", exam.getName());
            variables.put("studentGrade", "Note"); // TODO: Get individual grade
            variables.put("classAverage", results.getClassAverage());
            
            emailService.sendEmail(
                student.getEmail(),
                "Résultats d'examen disponibles",
                "exam-results",
                variables
            );
        });
        
        return savedResults;
    }

    @Override
    public List<Exam> findUpcomingExams(User professor) {
        return examRepository.findUpcomingExamsByProfessor(professor);
    }

    @Override
    public Map<String, Object> getExamStatistics(Long examId) {
        ExamResult results = examResultRepository.findByExam_Id(examId)
            .orElseThrow(() -> new RuntimeException("Résultats non trouvés"));
            
        Map<String, Object> stats = new HashMap<>();
        stats.put("average", results.getClassAverage());
        stats.put("highest", results.getHighestScore());
        stats.put("lowest", results.getLowestScore());
        stats.put("passRate", 
            (double) results.getPassedStudents() / results.getTotalStudents() * 100);
        
        return stats;
    }
}
```