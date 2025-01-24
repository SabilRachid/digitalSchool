package com.digital.school.repository;

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
}