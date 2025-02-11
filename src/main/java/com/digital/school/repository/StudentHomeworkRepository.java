package com.digital.school.repository;



import com.digital.school.model.Professor;
import com.digital.school.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.digital.school.model.StudentHomework;
import com.digital.school.model.User;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StudentHomeworkRepository extends JpaRepository<StudentHomework, Long> {
    List<StudentHomework> findByStudentAndStatusOrderByDueDateAsc(Student student, String status);
    
    @Query("SELECT COUNT(h) FROM StudentHomework h WHERE h.assignedBy = :professor AND h.status = 'SUBMITTED'")
    int countPendingGradingByProfessor(@Param("professor") Professor professor);
    
    @Query("SELECT COUNT(h) FROM StudentHomework h WHERE h.assignedBy = :professor AND h.dueDate >= :today")
    int countSubmissionsToday(@Param("professor") Professor professor, @Param("today") LocalDateTime today);

    @Query("SELECT h FROM StudentHomework h WHERE h.student = :student AND h.status = 'SUBMITTED' ORDER BY h.dueDate ASC")
    List<StudentHomework> findLateHomework(Student student);

    @Query("SELECT h FROM StudentHomework h WHERE h.student = :student ORDER BY h.dueDate DESC")
    List<StudentHomework> findByStudent(Student student);

    @Query("SELECT count(h) FROM StudentHomework h WHERE h.student = :student AND h.status = 'PENDING'")
    int countPendingHomework(Student student);

    @Query("SELECT count(h) FROM StudentHomework h WHERE h.student = :student AND h.dueDate > CURRENT_TIMESTAMP")
    int countByStudent(Student student);

    @Query("SELECT count(h) FROM StudentHomework h WHERE h.student = :student AND h.status = 'COMPLETED'")
    int countCompletedHomework(Student student);

    @Query("SELECT count(h) FROM StudentHomework h WHERE h.student = :student AND h.status = 'SUBMITTED'")
    int countLateHomework(Student student);

    @Query("SELECT h FROM StudentHomework h WHERE h.student = :student AND h.dueDate > :startOfYear")
    Collection<StudentHomework> findByStudentAndDueDateAfter(Student student, LocalDateTime startOfYear);

    @Query("SELECT h FROM StudentHomework h WHERE h.student = :student AND h.status = 'PENDING'")
    Iterable<StudentHomework> findPendingHomework(Student student);
}
