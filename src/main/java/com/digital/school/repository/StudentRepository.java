package com.digital.school.repository;

import com.digital.school.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByClasse_Id(Long classId); // Récupérer les élèves d'une classe

    @Query("SELECT COUNT(s) FROM Student s")
    Long countStudents();

    @Query("SELECT COUNT(s) FROM Student s WHERE s.enabled = true")
    Long countActiveStudents();

    @Query("SELECT COUNT(s) FROM Student s WHERE s.enabled = false")
    Long countPendingStudents();

    @Query("SELECT TO_CHAR(s.registrationDate, 'Mon YYYY') AS month, COUNT(s) " +
            "FROM Student s " +
            "WHERE s.registrationDate >= :startDate " +
            "GROUP BY TO_CHAR(s.registrationDate, 'Mon YYYY'), EXTRACT(YEAR FROM s.registrationDate), EXTRACT(MONTH FROM s.registrationDate) " +
            "ORDER BY EXTRACT(YEAR FROM s.registrationDate) ASC, EXTRACT(MONTH FROM s.registrationDate) ASC")
    List<Object[]> findRegistrationsAfter(@Param("startDate") LocalDateTime startDate);



}

