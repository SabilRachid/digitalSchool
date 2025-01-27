package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.Message;
import com.digital.school.model.User;
import com.digital.school.repository.MessageRepository;
import com.digital.school.service.MessageService;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public List<Message> findAllUserMessages(User user) {
        return messageRepository.findAllUserMessages(user);
    }

    @Override
    public List<Map<String, Object>> getUserMessagesAsMap(User user) {
        return findAllUserMessages(user).stream()
            .map(message -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", message.getId());
                map.put("subject", message.getSubject());
                map.put("content", message.getContent());
                map.put("sentAt", message.getSentAt());
                map.put("isRead", message.isRead());
                
                map.put("sender", Map.of(
                    "id", message.getSender().getId(),
                    "name", message.getSender().getFirstName() + " " + message.getSender().getLastName()
                ));
                
                map.put("recipient", Map.of(
                    "id", message.getRecipient().getId(),
                    "name", message.getRecipient().getFirstName() + " " + message.getRecipient().getLastName()
                ));
                
                return map;
            })
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Message> findById(Long id) {
        return messageRepository.findById(id);
    }

    @Override
    @Transactional
    public Message save(Message message) {
        return messageRepository.save(message);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        messageRepository.deleteById(id);
    }

    @Override
    public long countUnreadMessages(User user) {
        return messageRepository.countUnreadMessages(user);
    }

    @Override
    public Object findRecentMessages(User professor, int i) {
        return null;
    }
}