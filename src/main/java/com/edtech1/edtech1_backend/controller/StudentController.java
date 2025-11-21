package com.edtech1.edtech1_backend.controller;

import com.edtech1.edtech1_backend.model.User;
import com.edtech1.edtech1_backend.repository.SubmissionRepository;
import com.edtech1.edtech1_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private SubmissionRepository submissionRepository;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard-summary")
    public ResponseEntity<?> getDashboardSummary(Authentication authentication) {
        User student = getStudent(authentication);
        
        long completed = submissionRepository.countByStudentAndGradeIsNotNull(student);
        long pending = submissionRepository.countByStudentAndGradeIsNull(student);
        
        return ResponseEntity.ok(Map.of(
            "completedAssignments", completed,
            "pendingAssignments", pending,
            "averageGrade", 92.5 // Placeholder
        ));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        User student = getStudent(authentication);
        return ResponseEntity.ok(student);
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> updates, Authentication authentication) {
        User student = getStudent(authentication);
        
        if (updates.containsKey("name")) {
            student.setName((String) updates.get("name"));
        }
        // Add other fields as needed
        
        if (student != null) {
            userRepository.save(student);
        }
        return ResponseEntity.ok(student);
    }
    
    private User getStudent(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .filter(u -> u.getRole().name().equals("STUDENT"))
                .orElseThrow(() -> new RuntimeException("Student not found or unauthorized"));
    }
}
