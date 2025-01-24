package com.digital.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.digital.school.model.StudentHomework;
import com.digital.school.model.User;
import java.time.LocalDateTime;
import java.util.List;

public interface StudentHomeworkRepository extends JpaRepository<StudentHomework, Long> {
    List<StudentHomework> findByStudentAndStatusOrderByDueDateAsc(User student, String status);
    
    @Query("SELECT COUNT(h) FROM StudentHomework h WHERE h.student = ?1 AND h.status = 'PENDING' AND h.dueDate > CURRENT_TIMESTAMP")
    int countPendingHomework(User student);
    
    @Query("SELECT h FROM StudentHomework h WHERE h.student = ?1 AND h.dueDate > CURRENT_TIMESTAMP ORDER BY h.dueDate ASC")
    List<StudentHomework> findUpcomingHomework(User student);
}
