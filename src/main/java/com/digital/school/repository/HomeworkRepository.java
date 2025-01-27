package com.digital.school.repository;

import com.digital.school.model.Homework;
import com.digital.school.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    List<Homework> findPendingGradingByProfessor(User professor);
}