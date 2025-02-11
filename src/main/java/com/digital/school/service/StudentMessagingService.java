package com.digital.school.service;



import com.digital.school.model.*;

import java.util.List;
import java.util.Map;

public interface StudentMessagingService {
    List<Message> findStudentMessages(Student student);
    Message sendMessage(Message message);
    List<Message> findConversationWithProfessor(Student student, Professor professor);
    Meeting requestMeeting(Meeting meeting);
    List<Meeting> findUpcomingMeetings(Student student);
    void cancelMeetingRequest(Long meetingId);
    Map<String, Object> getMessagingStats(Student student);
}
