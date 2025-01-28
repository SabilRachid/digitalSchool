package com.digital.school.service;

import com.digital.school.model.Message;
import com.digital.school.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MessageService {
    List<Message> findAllUserMessages(User user);
    List<Map<String, Object>> getUserMessagesAsMap(User user);
    Optional<Message> findById(Long id);
    Message save(Message message);
    void deleteById(Long id);
    long countUnreadMessages(User user);

    //generate implementation
    Object findRecentMessages(User professor, int i);
}