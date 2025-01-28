package com.digital.school.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.Message;
import com.digital.school.model.User;
import com.digital.school.service.MessageService;
import com.digital.school.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private UserService userService;

    @GetMapping
    public String showMessages(HttpServletRequest request, Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", user);
        model.addAttribute("currentURI", request.getRequestURI());
        return "messages";
    }

    @GetMapping("/data")
    @ResponseBody
    public List<Map<String, Object>> getMessages(@AuthenticationPrincipal User user) {
        return messageService.getUserMessagesAsMap(user);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Message> getMessage(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return messageService.findById(id)
            .map(message -> {
                if (message.getRecipient().equals(user)) {
                    message.setRead(true);
                    messageService.save(message);
                }
                return ResponseEntity.ok(message);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> sendMessage(@RequestBody Message message, @AuthenticationPrincipal User sender) {
        try {
            message.setSender(sender);
            Message savedMessage = messageService.save(message);
            return ResponseEntity.ok(savedMessage);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de l'envoi: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return messageService.findById(id)
            .map(message -> {
                if (message.getSender().equals(user) || message.getRecipient().equals(user)) {
                    messageService.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                }
                return ResponseEntity.badRequest().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/unread-count")
    @ResponseBody
    public Map<String, Long> getUnreadCount(@AuthenticationPrincipal User user) {
        return Map.of("count", messageService.countUnreadMessages(user));
    }
    
    
    @GetMapping("/recipients")
    @ResponseBody
    public List<Map<String, Object>> getRecipients() {
        return userService.findAll(PageRequest.of(0, 1000)).stream()
            .map(user -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", user.getId());
                map.put("firstName", user.getFirstName());
                map.put("lastName", user.getLastName());
                map.put("email", user.getEmail());
                return map;
            })
            .collect(Collectors.toList());
    }
}