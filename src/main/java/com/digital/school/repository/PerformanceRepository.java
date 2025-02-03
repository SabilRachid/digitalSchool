package com.digital.school.repository;

import com.digital.school.model.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    Optional<Performance> findByStudentId(Long studentId);

    @Query("SELECT p FROM Performance p WHERE p.student.classe.id = :classId ORDER BY p.averageGrade DESC")
    List<Performance> findAllByClasseOrderByAverageDesc(@Param("classId") Long classId);


    @Query("SELECT c.level.name, AVG(p.averageGrade) FROM Performance p " +
               "JOIN p.student s " +
               "JOIN s.classe c " +
               "GROUP BY c.level.name")
    List<Object[]> findAveragePerformanceByLevel();



    @Query("SELECT p.subject.name, AVG(p.successRate) FROM Performance p " +
               "JOIN p.student s " +
               "GROUP BY p.subject.name")
    List<Object[]> findSuccessRateBySubject();
}
