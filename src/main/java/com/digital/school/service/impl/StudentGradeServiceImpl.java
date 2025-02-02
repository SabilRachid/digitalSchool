package com.digital.school.service.impl;

import com.digital.school.model.Student;
import com.digital.school.model.StudentGrade;
import com.digital.school.model.User;
import com.digital.school.repository.StudentGradeRepository;
import com.digital.school.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentGradeServiceImpl {
    
        @Autowired
        private StudentGradeRepository studentGradeRepository;

        @Autowired
        private PerformanceService performanceService;

        public Double calculateAverageGrade(Student student) {
        return studentGradeRepository.calculateAverageGrade(Optional.ofNullable(student));

        }
        public StudentGrade assignGrade(StudentGrade studentGrade) {
            studentGrade.setValue(studentGrade.getValue()); // Met automatiquement le grade
            return studentGradeRepository.save(studentGrade);
        }

        public List<StudentGrade> getGradesByStudent(Long studentId) {
            return studentGradeRepository.findByStudentId(studentId);
        }

        public List<StudentGrade> getGradesBySubject(Long subjectId) {
            return studentGradeRepository.findBySubjectId(subjectId);
        }
}
