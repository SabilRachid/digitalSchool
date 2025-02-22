package com.digital.school.repository;

import com.digital.school.model.StudentExam;
import com.digital.school.model.User;
import com.digital.school.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentExamRepository extends JpaRepository<StudentExam, Long> {

    List<StudentExam> findByStudent(User student);

    List<StudentExam> findByExam(Exam exam);

    Optional<StudentExam> findByStudentAndExam(User student, Exam exam);

    // Les méthodes de calcul de score ont été supprimées, puisque le score n'est plus présent dans StudentExam.
}
