package com.edtech1.edtech1_backend.controller;

import com.edtech1.edtech1_backend.model.Message;
import com.edtech1.edtech1_backend.model.User;
import com.edtech1.edtech1_backend.repository.MessageRepository;
import com.edtech1.edtech1_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/contacts")
    public ResponseEntity<?> getContacts(Authentication authentication) {
        // For now, return all users except self
        User currentUser = getUser(authentication);
        List<User> contacts = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(contacts);
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> payload, Authentication authentication) {
        User sender = getUser(authentication);
        Long receiverId = Long.parseLong(payload.get("receiverId").toString());
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
                
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent((String) payload.get("content"));
        
        messageRepository.save(message);
        return ResponseEntity.ok("Message sent");
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<?> getStudentChatHistory(@PathVariable Long id, Authentication authentication) {
        if (id == null) return ResponseEntity.badRequest().body("ID is required");
        // Chat between current user (Teacher) and Student (id)
        User currentUser = getUser(authentication);
        User student = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        return getChatHistory(currentUser, student);
    }

    @GetMapping("/teacher/{id}")
    public ResponseEntity<?> getTeacherChatHistory(@PathVariable Long id, Authentication authentication) {
        if (id == null) return ResponseEntity.badRequest().body("ID is required");
        // Chat between current user (Student) and Teacher (id)
        User currentUser = getUser(authentication);
        User teacher = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        
        return getChatHistory(currentUser, teacher);
    }
    
    private ResponseEntity<?> getChatHistory(User user1, User user2) {
        List<Message> sent = messageRepository.findBySenderAndReceiver(user1, user2);
        List<Message> received = messageRepository.findByReceiverAndSender(user1, user2);
        
        sent.addAll(received);
        // Sort by time
        sent.sort((m1, m2) -> m1.getSentAt().compareTo(m2.getSentAt()));
        
        return ResponseEntity.ok(sent);
    }
    
    private User getUser(Authentication authentication) {
        if (authentication == null) {
             // Return first user for testing
             return userRepository.findAll().stream().findFirst().orElseThrow();
        }
        // return userRepository.findByEmail(authentication.getName()).orElseThrow(...);
        return userRepository.findAll().stream().findFirst().orElseThrow();
    }
}
