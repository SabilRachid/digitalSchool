package com.digital.school.controller.rest.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.*;
import com.digital.school.service.StudentMessagingService;
import com.digital.school.service.UserService;

import java.util.Map;

@Controller
@RequestMapping("/student/messages")
public class StudentMessagingController {

    @Autowired
    private StudentMessagingService messagingService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String showMessages(@AuthenticationPrincipal User student, Model model) {
        model.addAttribute("messages", messagingService.findStudentMessages(student));
        model.addAttribute("stats", messagingService.getMessagingStats(student));
        model.addAttribute("professors", userService.findProfessors(student.getClasse()));
        return "student/messages";
    }

    @GetMapping("/conversation/{professorId}")
    public String showConversation(
            @PathVariable Long professorId,
            @AuthenticationPrincipal User student,
            Model model) {
        User professor = userService.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professeur non trouvé"));

        model.addAttribute("messages",
                messagingService.findConversationWithProfessor(student, professor));
        model.addAttribute("professor", professor);
        return "student/conversation";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> sendMessage(
            @RequestBody Message message,
            @AuthenticationPrincipal User student) {
        try {
            message.setSender(student);
            Message sent = messagingService.sendMessage(message);
            return ResponseEntity.ok(sent);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/meeting")
    @ResponseBody
    public ResponseEntity<?> requestMeeting(
            @RequestBody Meeting meeting,
            @AuthenticationPrincipal User student) {
        try {
            meeting.setRequestedBy(student);
            Meeting scheduled = messagingService.requestMeeting(meeting);
            return ResponseEntity.ok(scheduled);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/meeting/{id}")
    @ResponseBody
    public ResponseEntity<?> cancelMeeting(@PathVariable Long id) {
        try {
            messagingService.cancelMeetingRequest(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStats(@AuthenticationPrincipal User student) {
        return ResponseEntity.ok(messagingService.getMessagingStats(student));
    }
}