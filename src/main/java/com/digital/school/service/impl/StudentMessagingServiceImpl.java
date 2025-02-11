package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.*;
import com.digital.school.repository.*;
import com.digital.school.service.StudentMessagingService;
import com.digital.school.service.EmailService;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class StudentMessagingServiceImpl implements StudentMessagingService {

    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private MeetingRepository meetingRepository;
    
    @Autowired
    private EmailService emailService;

    @Override
    public List<Message> findStudentMessages(Student student) {
        return messageRepository.findByStudentOrderByDateDesc(student);
    }

    @Override
    @Transactional
    public Message sendMessage(Message message) {
        message.setSentAt(LocalDateTime.now());
        message = messageRepository.save(message);
        
        // Envoyer une notification par email au destinataire
        Map<String, Object> variables = new HashMap<>();
        variables.put("senderName", 
            message.getSender().getFirstName() + " " + 
            message.getSender().getLastName());
        variables.put("messageSubject", message.getSubject());
        
        emailService.sendEmail(
            message.getRecipient().getEmail(),
            "Nouveau message reçu",
            "message-notification",
            variables
        );
        
        return message;
    }

    @Override
    public List<Message> findConversationWithProfessor(Student student, Professor professor) {
        return messageRepository.findConversation(student, professor);
    }

    @Override
    @Transactional
    public Meeting requestMeeting(Meeting meeting) {
        meeting.setStatus("PENDING");
        meeting.setRequestedAt(LocalDateTime.now());
        meeting = meetingRepository.save(meeting);
        
        // Notifier le professeur
        User professor = meeting.getParticipants().stream()
            .filter(p -> p.getRoles().stream()
                .anyMatch(r -> r.getName().toString().equals("ROLE_PROFESSOR")))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Professeur non trouvé"));
            
        Map<String, Object> variables = new HashMap<>();
        variables.put("studentName", 
            meeting.getRequestedBy().getFirstName() + " " + 
            meeting.getRequestedBy().getLastName());
        variables.put("meetingDate", meeting.getStartTime());
        variables.put("meetingReason", meeting.getDescription());
        
        emailService.sendEmail(
            professor.getEmail(),
            "Nouvelle demande de rendez-vous",
            "meeting-request",
            variables
        );
        
        return meeting;
    }

    @Override
    public List<Meeting> findUpcomingMeetings(Student student) {
        return meetingRepository.findUpcomingMeetingsByStudent(student);
    }

    @Override
    @Transactional
    public void cancelMeetingRequest(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));
            
        // Notifier le professeur
        User professor = meeting.getParticipants().stream()
            .filter(p -> p.getRoles().stream()
                .anyMatch(r -> r.getName().toString().equals("ROLE_PROFESSOR")))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Professeur non trouvé"));
            
        Map<String, Object> variables = new HashMap<>();
        variables.put("studentName", 
            meeting.getRequestedBy().getFirstName() + " " + 
            meeting.getRequestedBy().getLastName());
        variables.put("meetingDate", meeting.getStartTime());
        
        emailService.sendEmail(
            professor.getEmail(),
            "Annulation de rendez-vous",
            "meeting-cancellation",
            variables
        );
        
        meetingRepository.deleteById(meetingId);
    }

    @Override
    public Map<String, Object> getMessagingStats(Student student) {
        Map<String, Object> stats = new HashMap<>();
        
        // Messages non lus
        long unreadMessages = messageRepository.countUnreadMessages(student);
        stats.put("unreadMessages", unreadMessages);
        
        // Rendez-vous en attente
        long pendingMeetings = meetingRepository.countPendingMeetings(student);
        stats.put("pendingMeetings", pendingMeetings);
        
        // Temps moyen de réponse des professeurs
        Double avgResponseTime = messageRepository.calculateAverageResponseTime(student);
        stats.put("averageResponseTime", avgResponseTime != null ? avgResponseTime : 0);
        
        return stats;
    }
}
