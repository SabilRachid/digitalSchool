package com.digital.school.repository;

import com.digital.school.model.Homework;
import com.digital.school.model.Professor;
import com.digital.school.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.digital.school.model.User;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    List<Homework> findByStudentAndStatusOrderByDueDateAsc(User student, String status);

    @Query("SELECT COUNT(h) FROM Homework h WHERE h.student = ?1 AND h.status = 'PENDING' AND h.dueDate > CURRENT_TIMESTAMP")
    int countPendingHomework(Student student);

    @Query("SELECT h FROM Homework h WHERE h.student = ?1 AND h.dueDate > CURRENT_TIMESTAMP ORDER BY h.dueDate ASC")
    List<Homework> findUpcomingHomework(Student student);


    @Query("SELECT COUNT(h) FROM Homework h WHERE h.professor = :professor AND h.status = 'SUBMITTED'")
    int countPendingGradingByProfessor(@Param("professor") Professor professor);

    @Query("SELECT COUNT(h) FROM Homework h WHERE h.professor = :professor")
    int countSubmissionsToday(@Param("professor") Professor professor);

    //implement this query in ai
    @Query("SELECT h FROM Homework h WHERE h.professor = :professor AND h.status = 'SUBMITTED' ORDER BY h.dueDate ASC")
    List<Homework> findPendingGradingByProfessor(@Param("professor") Professor professor);


    //implement this query in ai
    @Query("SELECT h FROM Homework h WHERE h.student = :student AND h.status = 'SUBMITTED' ORDER BY h.dueDate DESC")
    List<Homework> findByStudentAndStatusOrderByDueDateDesc(@Param("student") Student student);

    //implement this query in ai
    @Query("SELECT h FROM Homework h WHERE h.student = :student AND h.status = :submitted ORDER BY h.dueDate desc")
    List<Homework> findByStudentAndStatusOrderByDueDateDesc(Student student, String submitted);

    List<Homework> findByProfessor(User professor);
}
