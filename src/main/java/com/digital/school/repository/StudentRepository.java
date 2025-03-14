package com.digital.school.repository;

import com.digital.school.model.Homework;
import com.digital.school.model.Professor;
import com.digital.school.model.Student;
import com.digital.school.model.enumerated.EvaluationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    
     Optional<Student> findById(Long studentId); // Récupérer les élèves d'une classe

    @Query("SELECT s FROM Student s WHERE s.classe.id = :classId")
    List<Student> getStudentsByClasseId(Long classId);

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


    @Query("SELECT COUNT(s) FROM Student s WHERE s.classe.id = :classeId")
    int countByClasse(Long classeId);

    @Query("SELECT s FROM Student s WHERE s.classe.id = :id")
    List<Student> findByClasseId(@Param("id") Long id);


    interface HomeworkRepository extends JpaRepository<Homework, Long> {

        Optional<Homework> findByIdAndProfessor(Long id, Professor professor);

        @Query("SELECT new map(" +
                "h.id as id, " +
                "h.title as title, " +
                "s.name as subjectName, " +
                "c.name as classeName, " +
                "h.dueDate as dueDate, " +
                "h.status as status) " +
                "FROM Homework h " +
                "JOIN h.subject s " +
                "JOIN h.classe c " +
                "WHERE (:classId IS NULL OR c.id = :classId) " +
                "AND (:year IS NULL OR FUNCTION('YEAR', h.dueDate) = :year) " +
                "AND (:month IS NULL OR FUNCTION('MONTH', h.dueDate) = :month)")
        List<Map<String, Object>> findAllAsMap(@Param("classId") Long classId,
                                               @Param("year") Integer year,
                                               @Param("month") Integer month);

        List<Homework> findByProfessor(Professor professor);

        @Query("SELECT COUNT(h) FROM Homework h WHERE h.professor = :professor AND h.status = :status")
        int countPendingGradingByProfessor(@Param("professor") Professor professor,
                                           @Param("status") EvaluationStatus status);

        @Query("SELECT COUNT(h) FROM Homework h WHERE h.professor = :professor")
        int countSubmissionsToday(@Param("professor") Professor professor);

        @Query("SELECT h FROM Homework h WHERE h.professor = :professor AND h.status = :status ORDER BY h.dueDate ASC")
        List<Homework> findPendingGradingByProfessor(@Param("professor") Professor professor,
                                                     @Param("status") EvaluationStatus status);
    }
}

