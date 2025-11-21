package com.edtech1.edtech1_backend.util;

import com.edtech1.edtech1_backend.model.*;
import com.edtech1.edtech1_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClassroomRepository classroomRepository;
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private QuizQuestionRepository quizQuestionRepository;
    @Autowired
    private CareerStoryRepository careerStoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            // Create Users
            User student = new User();
            student.setName("John Student");
            student.setEmail("student@test.com");
            student.setPassword(passwordEncoder.encode("password"));
            student.setRole(Role.STUDENT);
            student.setStatus("ACTIVE");
            userRepository.save(student);

            User teacher = new User();
            teacher.setName("Jane Teacher");
            teacher.setEmail("teacher@test.com");
            teacher.setPassword(passwordEncoder.encode("password"));
            teacher.setRole(Role.TEACHER);
            teacher.setStatus("ACTIVE");
            userRepository.save(teacher);
            
            // Create Classroom
            Classroom classroom = new Classroom();
            classroom.setName("Math 101");
            classroom.setTeacher(teacher);
            classroom.setStudents(List.of(student));
            classroomRepository.save(classroom);
            
            // Create Assignment
            Assignment assignment = new Assignment();
            assignment.setTitle("Algebra Homework");
            assignment.setDescription("Solve chapter 5 problems.");
            assignment.setDueDate(LocalDateTime.now().plusDays(7));
            assignment.setTeacher(teacher);
            assignmentRepository.save(assignment);
            
            // Create Submission (Pending)
            Submission submission = new Submission();
            submission.setAssignment(assignment);
            submission.setStudent(student);
            submission.setFilePath("homework_v1.pdf");
            submissionRepository.save(submission);
            
            // Create Message
            Message msg = new Message();
            msg.setSender(teacher);
            msg.setReceiver(student);
            msg.setContent("Welcome to the class!");
            messageRepository.save(msg);
            
            // Create Notification
            Notification notif = new Notification();
            notif.setUser(student);
            notif.setMessage("New assignment posted: Algebra Homework");
            notif.setRead(false);
            notificationRepository.save(notif);
            
            // Create Quiz Questions
            QuizQuestion q1 = new QuizQuestion();
            q1.setQuestion("What do you enjoy more?");
            q1.setOptions(List.of("Solving Puzzles", "Painting"));
            q1.setCorrectOption("Solving Puzzles"); // Just for schema, not used in logic yet
            quizQuestionRepository.save(q1);
            
            QuizQuestion q2 = new QuizQuestion();
            q2.setQuestion("Do you prefer working alone or in a team?");
            q2.setOptions(List.of("Alone", "Team"));
            q2.setCorrectOption("Team");
            quizQuestionRepository.save(q2);
            
            // Create Career Stories
            CareerStory s1 = new CareerStory();
            s1.setTitle("My Life as an Engineer");
            s1.setAuthor("Alice Engineer");
            s1.setContent("Engineering is about solving problems...");
            careerStoryRepository.save(s1);
            
            System.out.println("Full mock data seeded!");
        }
    }
}
