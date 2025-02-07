package com.digital.school.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.digital.school.model.StudentHomework;
import com.digital.school.model.User;
import java.time.LocalDateTime;
import java.util.List;

public interface StudentHomeworkRepository extends JpaRepository<StudentHomework, Long> {
    List<StudentHomework> findByStudentAndStatusOrderByDueDateAsc(User student, String status);
    
    @Query("SELECT COUNT(h) FROM StudentHomework h WHERE h.assignedBy = :professor AND h.status = 'SUBMITTED'")
    int countPendingGradingByProfessor(@Param("professor") User professor);
    
    @Query("SELECT COUNT(h) FROM StudentHomework h WHERE h.assignedBy = :professor AND h.submissionDate >= :today")
    int countSubmissionsToday(@Param("professor") User professor, @Param("today") LocalDateTime today);
}
