package com.digital.school.service;

import com.digital.school.model.Meeting;
import com.digital.school.model.User;
import java.util.List;
import java.util.Optional;

public interface MeetingService {
    List<Meeting> findByProfessor(User professor);
    Optional<Meeting> findById(Long id);
    Meeting save(Meeting meeting);
    void deleteById(Long id);
    void sendReminders(Long id);
    List<Meeting> findUpcomingMeetings(User professor);
    boolean isTimeSlotAvailable(Meeting meeting);
}
