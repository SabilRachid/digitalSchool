package com.digital.school.service;

public interface PerformanceService {

    void updateStudentPerformance(Long studentId);

    void finalizeClassPerformance(Long subjectId, String title, Long classId);

}