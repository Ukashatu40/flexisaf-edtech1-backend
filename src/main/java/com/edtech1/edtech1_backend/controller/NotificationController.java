package com.edtech1.edtech1_backend.controller;

import com.edtech1.edtech1_backend.model.Notification;
import com.edtech1.edtech1_backend.model.User;
import com.edtech1.edtech1_backend.repository.NotificationRepository;
import com.edtech1.edtech1_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getNotifications(Authentication authentication) {
        User user = getUser(authentication);
        List<Notification> notifications = notificationRepository.findByUser(user);
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/mark-read/{id}")
    public ResponseEntity<?> markRead(@PathVariable Long id) {
        if (id == null) return ResponseEntity.badRequest().body("ID is required");
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setRead(true);
        notificationRepository.save(notification);
        
        return ResponseEntity.ok("Marked as read");
    }
    
    private User getUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found or unauthorized"));
    }
}
