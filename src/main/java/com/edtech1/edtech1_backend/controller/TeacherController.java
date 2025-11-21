package com.edtech1.edtech1_backend.controller;

import com.edtech1.edtech1_backend.model.Classroom;
import com.edtech1.edtech1_backend.model.User;
import com.edtech1.edtech1_backend.repository.ClassroomRepository;
import com.edtech1.edtech1_backend.repository.SubmissionRepository;
import com.edtech1.edtech1_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private SubmissionRepository submissionRepository;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard-stats")
    public ResponseEntity<?> getDashboardStats(Authentication authentication) {
        // Mock auth for now if null, or use a specific teacher
        User teacher = getTeacher(authentication);
        
        List<Classroom> classes = classroomRepository.findByTeacher(teacher);
        int totalStudents = classes.stream().mapToInt(c -> c.getStudents().size()).sum();
        
        // Pending reviews: submissions where grade is null
        // This is a simplification; ideally we filter by teacher's assignments
        long pendingReviews = submissionRepository.findAll().stream()
                .filter(s -> s.getAssignment().getTeacher().getId().equals(teacher.getId()) && s.getGrade() == null)
                .count();

        return ResponseEntity.ok(Map.of(
            "pendingReviews", pendingReviews,
            "totalStudents", totalStudents,
            "progressRate", 85.5, // Placeholder logic for now
            "recentSubmissions", List.of() // Placeholder
        ));
    }

    @GetMapping("/classes")
    public ResponseEntity<?> getClasses(Authentication authentication) {
        User teacher = getTeacher(authentication);
        List<Classroom> classes = classroomRepository.findByTeacher(teacher);
        return ResponseEntity.ok(classes);
    }
    
    private User getTeacher(Authentication authentication) {
        // If auth is null (dev mode), return the first teacher found
        if (authentication == null) {
            return userRepository.findAll().stream()
                    .filter(u -> u.getRole().name().equals("TEACHER"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No teacher found"));
        }
        // In real auth, find by email
        // return userRepository.findByEmail(authentication.getName()).orElseThrow(...);
         return userRepository.findAll().stream()
                    .filter(u -> u.getRole().name().equals("TEACHER"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No teacher found"));
    }
}
