package com.edtech1.edtech1_backend.service;

import com.edtech1.edtech1_backend.model.Role;
import com.edtech1.edtech1_backend.model.User;
import com.edtech1.edtech1_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileStorageService fileStorageService;

    // Handles the business logic for registering a new user. We check for existing
    // emails, encode the password, and handle file uploads for teachers.
    public User registerUser(String name, String email, String password, String roleStr,
            String teacherId, String schoolName, MultipartFile credentialFile) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        Role role = Role.valueOf(roleStr.toUpperCase());
        user.setRole(role);

        if (role == Role.STUDENT) {
            user.setStatus("ACTIVE");
        } else if (role == Role.TEACHER) {
            user.setStatus("PENDING");
            user.setTeacherId(teacherId);
            user.setSchoolName(schoolName);

            if (credentialFile != null && !credentialFile.isEmpty()) {
                String fileName = fileStorageService.storeFile(credentialFile);
                user.setCredentialFilePath(fileName);
            }
        } else {
            user.setStatus("ACTIVE"); // Default for others if any
        }

        return userRepository.save(user);
    }

    // Validates user credentials. We also check if the account is PENDING approval
    // before allowing login.
    public User loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if ("PENDING".equals(user.getStatus())) {
            throw new RuntimeException("Account is pending approval");
        }

        return user;
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
