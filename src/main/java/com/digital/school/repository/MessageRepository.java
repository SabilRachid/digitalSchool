package com.digital.school.repository;

import com.digital.school.model.Professor;
import com.digital.school.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.digital.school.model.Message;
import com.digital.school.model.User;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderOrderBySentAtDesc(User sender);
    List<Message> findByRecipientOrderBySentAtDesc(User recipient);
    
    @Query("SELECT m FROM Message m WHERE (m.sender = :user OR m.recipient = :user) ORDER BY m.sentAt DESC")
    List<Message> findAllUserMessages(@Param("user") User user);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.recipient = :user AND m.isRead = false")
    long countUnreadMessages(@Param("user") User user);

    @Query("SELECT m FROM Message m WHERE m.sender = :sender AND m.recipient = :recipient OR m.sender = :recipient AND m.recipient = :sender ORDER BY m.sentAt DESC")
    List<Message> findByStudentOrderByDateDesc(User student);

    @Query("SELECT m FROM Message m WHERE (m.sender = :student AND m.recipient = :professor) OR (m.sender = :professor AND m.recipient = :student) ORDER BY m.sentAt DESC")
    List<Message> findConversation(Student student, Professor professor);

    @Query("SELECT AVG(m.readAt - m.sentAt ) FROM Message m WHERE m.sender = :student")
    Double calculateAverageResponseTime(Student student);
}