package com.digital.school.service;



import com.digital.school.model.Message;
import com.digital.school.model.Meeting;
import com.digital.school.model.User;
import java.util.List;
import java.util.Map;

public interface StudentMessagingService {
    List<Message> findStudentMessages(User student);
    Message sendMessage(Message message);
    List<Message> findConversationWithProfessor(User student, User professor);
    Meeting requestMeeting(Meeting meeting);
    List<Meeting> findUpcomingMeetings(User student);
    void cancelMeetingRequest(Long meetingId);
    Map<String, Object> getMessagingStats(User student);
}
