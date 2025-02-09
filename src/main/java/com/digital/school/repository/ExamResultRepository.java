package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.digital.school.model.ExamResult;
import com.digital.school.model.Exam;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    Optional<ExamResult> findByExam(Exam exam);

    @Query("SELECT er FROM ExamResult er WHERE er.exam.id = :examId")
    Optional<ExamResult> findByExamId(Long examId);
}