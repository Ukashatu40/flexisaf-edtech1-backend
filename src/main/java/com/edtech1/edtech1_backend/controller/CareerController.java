package com.edtech1.edtech1_backend.controller;

import com.edtech1.edtech1_backend.model.CareerStory;
import com.edtech1.edtech1_backend.model.QuizQuestion;
import com.edtech1.edtech1_backend.repository.CareerStoryRepository;
import com.edtech1.edtech1_backend.repository.QuizQuestionRepository;
import com.edtech1.edtech1_backend.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CareerController {

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;
    
    @Autowired
    private CareerStoryRepository careerStoryRepository;

    @Autowired
    private com.edtech1.edtech1_backend.repository.UserRepository userRepository;

    @GetMapping("/career-quiz/questions")
    public ResponseEntity<?> getQuizQuestions() {
        List<QuizQuestion> questions = quizQuestionRepository.findAll();
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/career-quiz/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody Map<String, Object> answers) {
        // Simple logic: if more than half correct, suggest Engineering, else Art
        // For now just return a static result based on input or random
        return ResponseEntity.ok(Map.of("result", "Engineering", "message", "You seem to like solving problems!"));
    }

    @GetMapping("/career-stories")
    public ResponseEntity<?> getCareerStories() {
        List<CareerStory> stories = careerStoryRepository.findAll();
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/career-stories/{id}")
    public ResponseEntity<?> getCareerStory(@PathVariable Long id) {
        if (id == null) return ResponseEntity.badRequest().body("ID is required");
        CareerStory story = careerStoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Story not found"));
        return ResponseEntity.ok(story);
    }

    // New private helper method for authentication logic
    private User getUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found or unauthorized"));
    }

    @PostMapping("/mentor-chat/send")
    public ResponseEntity<?> sendMentorMessage(@RequestBody Map<String, String> message) {
        // In real app, save to MessageRepository with a specific Mentor user
        return ResponseEntity.ok("Message sent");
    }

    @GetMapping("/mentor-chat/history")
    public ResponseEntity<?> getMentorChatHistory() {
        // Mock history for now, or implement if we have a Mentor entity/role
        return ResponseEntity.ok(List.of(
            Map.of("sender", "Mentor", "message", "Hello! How can I help?"),
            Map.of("sender", "Student", "message", "I have a question about engineering.")
        ));
    }
}
