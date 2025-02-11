package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digital.school.model.Meeting;
import com.digital.school.model.User;
import com.digital.school.repository.MeetingRepository;
import com.digital.school.service.MeetingService;
import com.digital.school.service.EmailService;
import com.digital.school.service.SMSService;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SMSService smsService;

    @Override
    public List<Meeting> findByProfessor(User professor) {
        return meetingRepository.findByOrganizer(professor);
    }

    @Override
    public Optional<Meeting> findById(Long id) {
        return meetingRepository.findById(id);
    }

    @Override
    @Transactional
    public Meeting save(Meeting meeting) {
        if (!isTimeSlotAvailable(meeting)) {
            throw new RuntimeException("Ce créneau horaire n'est pas disponible");
        }
        
        boolean isNew = meeting.getId() == null;
        meeting = meetingRepository.save(meeting);
        
        if (isNew) {
            // Notifier les participants
            Meeting finalMeeting = meeting;
            meeting.getParticipants().forEach(participant -> {
                // Email
                Map<String, Object> variables = new HashMap<>();
                variables.put("organizerName", 
                    finalMeeting.getOrganizer().getFirstName() + " " +
                    finalMeeting.getOrganizer().getLastName());
                variables.put("meetingDate", finalMeeting.getStartTime());
                variables.put("meetingLocation", finalMeeting.getLocation());
                
                emailService.sendEmail(
                    participant.getEmail(),
                    "Nouvelle réunion programmée",
                    "meeting-invitation",
                    variables
                );
                
                // SMS si numéro disponible
                if (participant.getPhone() != null) {
                    String message = String.format(
                        "Nouvelle réunion le %s avec %s. Lieu: %s",
                        finalMeeting.getStartTime(),
                        finalMeeting.getOrganizer().getLastName(),
                        finalMeeting.getLocation()
                    );
                    smsService.sendSMS(participant.getPhone(), message);
                }
            });
        }
        
        return meeting;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Meeting meeting = findById(id)
            .orElseThrow(() -> new RuntimeException("Réunion non trouvée"));
            
        // Notifier les participants de l'annulation
        meeting.getParticipants().forEach(participant -> {
            Map<String, Object> variables = new HashMap<>();
            variables.put("meetingDate", meeting.getStartTime());
            
            emailService.sendEmail(
                participant.getEmail(),
                "Réunion annulée",
                "meeting-cancellation",
                variables
            );
        });
        
        meetingRepository.deleteById(id);
    }

    @Override
    public void sendReminders(Long id) {
        Meeting meeting = findById(id)
            .orElseThrow(() -> new RuntimeException("Réunion non trouvée"));
            
        meeting.getParticipants().forEach(participant -> {
            Map<String, Object> variables = new HashMap<>();
            variables.put("meetingDate", meeting.getStartTime());
            variables.put("meetingLocation", meeting.getLocation());
            
            emailService.sendEmail(
                participant.getEmail(),
                "Rappel de réunion",
                "meeting-reminder",
                variables
            );
        });
    }

    @Override
    public List<Meeting> findUpcomingMeetings(User professor) {
        return meetingRepository.findUpcomingMeetingsByOrganizer(
            professor, 
            LocalDateTime.now()
        );
    }

    @Override
    public boolean isTimeSlotAvailable(Meeting meeting) {
        return meetingRepository.countOverlappingMeetings(
            meeting.getOrganizer(),
            meeting.getStartTime(),
            meeting.getEndTime(),
            meeting.getId()
        ) == 0;
    }
}
