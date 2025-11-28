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

        // Recent submissions
        List<com.edtech1.edtech1_backend.model.Submission> recentSubmissions = submissionRepository.findTop5ByAssignmentTeacherOrderBySubmittedAtDesc(teacher);
        
        // Progress Rate (Graded / Total Submissions)
        List<com.edtech1.edtech1_backend.model.Submission> allSubmissions = submissionRepository.findByAssignmentTeacher(teacher);
        long totalSubmissions = allSubmissions.size();
        long gradedSubmissions = allSubmissions.stream().filter(s -> s.getGrade() != null).count();
        
        double progressRate = totalSubmissions == 0 ? 0 : ((double) gradedSubmissions / totalSubmissions) * 100;

        return ResponseEntity.ok(Map.of(
            "pendingReviews", pendingReviews,
            "totalStudents", totalStudents,
            "progressRate", progressRate,
            "recentSubmissions", recentSubmissions
        ));
    }

    @GetMapping("/classes")
    public ResponseEntity<?> getClasses(Authentication authentication) {
        User teacher = getTeacher(authentication);
        List<Classroom> classes = classroomRepository.findByTeacher(teacher);
        return ResponseEntity.ok(classes);
    }
    
    private User getTeacher(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .filter(u -> u.getRole().name().equals("TEACHER"))
                .orElseThrow(() -> new RuntimeException("Teacher not found or unauthorized"));
    }
}
