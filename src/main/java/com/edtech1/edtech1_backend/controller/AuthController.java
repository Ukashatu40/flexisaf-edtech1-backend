package com.edtech1.edtech1_backend.controller;

import com.edtech1.edtech1_backend.model.Role;
import com.edtech1.edtech1_backend.model.User;
import com.edtech1.edtech1_backend.repository.UserRepository;
import com.edtech1.edtech1_backend.security.JwtTokenProvider;
import com.edtech1.edtech1_backend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileStorageService fileStorageService;

    // Handle user registration. We accept multipart data to allow file uploads for
    // teachers.
    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<?> register(@RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("role") String role,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        // Check if the email is already taken to prevent duplicate accounts
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.valueOf(role.toUpperCase()));

        if (Role.TEACHER.name().equalsIgnoreCase(role)) {
            // Teachers default to ACTIVE for now, but we might want to change this to
            // PENDING later if we add an admin approval step.
            user.setStatus("ACTIVE");

            // If the teacher uploaded a credential file, save it and link the path to their
            // profile
            if (file != null) {
                String path = fileStorageService.storeFile(file);
                user.setCredentialFilePath(path);
            }
        } else {
            // Students are automatically active upon registration
            user.setStatus("ACTIVE");
        }

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    // Authenticate the user and return a JWT token if successful
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            // Fetch the full user details to return along with the token
            User user = userRepository.findByEmail(email).orElseThrow();

            return ResponseEntity.ok(Map.of(
                    "token", jwt,
                    "user", user));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        // Return the current authenticated user's details.
        // If security is disabled or the user isn't logged in, this might be null.
        if (authentication != null) {
            return ResponseEntity.ok(authentication.getPrincipal());
        }
        return ResponseEntity.status(401).build();
    }

    // Allow users to update specific profile fields. Using PatchMapping for partial
    // updates.
    @PatchMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> updates, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updates.containsKey("managedCourses")) {
            user.setManagedCourses((java.util.List<String>) updates.get("managedCourses"));
        }

        // Allow name update here too if needed
        if (updates.containsKey("name")) {
            user.setName((String) updates.get("name"));
        }

        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Client side logout (clear token)
        return ResponseEntity.ok("Logged out");
    }
}
