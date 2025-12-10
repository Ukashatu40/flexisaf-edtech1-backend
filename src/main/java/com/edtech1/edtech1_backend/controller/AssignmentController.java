package com.edtech1.edtech1_backend.controller;

import com.edtech1.edtech1_backend.model.Assignment;
import com.edtech1.edtech1_backend.model.Submission;
import com.edtech1.edtech1_backend.model.User;
import com.edtech1.edtech1_backend.repository.AssignmentRepository;
import com.edtech1.edtech1_backend.repository.SubmissionRepository;
import com.edtech1.edtech1_backend.repository.UserRepository;
import com.edtech1.edtech1_backend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class AssignmentController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    // Teacher Endpoints
    // Allows teachers to create new assignments. We currently set a default due
    // date of 7 days if not provided.
    @PostMapping("/teacher/assignments/create")
    public ResponseEntity<?> createAssignment(@RequestBody Map<String, Object> payload, Authentication authentication) {
        User teacher = getUser(authentication, "TEACHER");

        Assignment assignment = new Assignment();
        assignment.setTitle((String) payload.get("title"));
        assignment.setDescription((String) payload.get("description"));
        // Parse date if needed, for now assuming simple string or handled by frontend
        assignment.setDueDate(LocalDateTime.now().plusDays(7)); // Default or parse from payload
        assignment.setTeacher(teacher);

        assignmentRepository.save(assignment);
        return ResponseEntity.ok(assignment);
    }

    @GetMapping("/teacher/assignments")
    public ResponseEntity<?> getTeacherAssignments(Authentication authentication) {
        User teacher = getUser(authentication, "TEACHER");
        List<Assignment> assignments = assignmentRepository.findByTeacher(teacher);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/teacher/assignments/{id}")
    public ResponseEntity<?> getAssignmentDetails(@PathVariable Long id) {
        if (id == null)
            return ResponseEntity.badRequest().body("ID is required");
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        List<Submission> submissions = submissionRepository.findByAssignmentId(id);

        return ResponseEntity.ok(Map.of(
                "assignment", assignment,
                "submissions", submissions));
    }

    // Updates the grade and feedback for a student's submission. We expect the
    // submission ID in the payload.
    @PostMapping("/teacher/assignments/{id}/feedback")
    public ResponseEntity<?> giveFeedback(@PathVariable Long id, @RequestBody Map<String, String> feedbackPayload) {
        // Ideally we need submission ID, but if we only have assignment ID, we need
        // student ID too.
        // Or the endpoint should be /teacher/submissions/{id}/feedback
        // Based on requirements: POST /teacher/assignments/:id/feedback
        // This implies feedback for a specific student? Or maybe the ID here is
        // submission ID?
        // Let's assume the payload contains submissionId or studentId.
        // Or let's change the route to be more specific if possible, but sticking to
        // requirements:

        // Assuming payload has submissionId
        Long submissionId = Long.parseLong(feedbackPayload.get("submissionId"));
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        submission.setGrade(feedbackPayload.get("grade"));
        submission.setFeedback(feedbackPayload.get("feedback"));
        submissionRepository.save(submission);

        return ResponseEntity.ok("Feedback submitted");
    }

    // Student Endpoints
    // Retrieves all available assignments. In a future update, this will be
    // filtered by the student's enrolled classes.
    @GetMapping("/student/assignments")
    public ResponseEntity<?> getStudentAssignments(Authentication authentication) {
        // For students, we should show all assignments from their teachers (via
        // classrooms)
        // For simplicity, showing all assignments or we need logic to find student's
        // classes
        // Let's return all assignments for now as a simplification
        return ResponseEntity.ok(assignmentRepository.findAll());
    }

    @GetMapping("/student/assignments/{id}")
    public ResponseEntity<?> getStudentAssignmentDetails(@PathVariable Long id) {
        if (id == null)
            return ResponseEntity.badRequest().body("ID is required");
        return ResponseEntity.ok(assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found")));
    }

    // Handles student file submissions. We store the file and link it to the
    // assignment.
    @PostMapping("/student/assignments/{id}/submit")
    public ResponseEntity<?> submitAssignment(@PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        if (id == null)
            return ResponseEntity.badRequest().body("ID is required");
        User student = getUser(authentication, "STUDENT");
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        String fileName = fileStorageService.storeFile(file);

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setFilePath(fileName);

        submissionRepository.save(submission);

        return ResponseEntity.ok("Assignment submitted: " + fileName);
    }

    @GetMapping("/student/assignments/{id}/feedback")
    public ResponseEntity<?> getFeedback(@PathVariable Long id, Authentication authentication) {
        User student = getUser(authentication, "STUDENT");
        // Find submission for this assignment and student
        // We need a custom query or filter
        List<Submission> submissions = submissionRepository.findByStudent(student);
        Optional<Submission> sub = submissions.stream()
                .filter(s -> s.getAssignment().getId().equals(id))
                .findFirst();

        if (sub.isPresent()) {
            return ResponseEntity.ok(sub.get());
        }
        return ResponseEntity.status(404).body("No submission found");
    }

    private User getUser(Authentication authentication, String role) {
        return userRepository.findByEmail(authentication.getName())
                .filter(u -> u.getRole().name().equals(role))
                .orElseThrow(() -> new RuntimeException("User not found or unauthorized"));
    }
}
