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

    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<?> register(@RequestParam("name") String name,
                                      @RequestParam("email") String email,
                                      @RequestParam("password") String password,
                                      @RequestParam("role") String role,
                                      @RequestParam(value = "file", required = false) MultipartFile file) {
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.valueOf(role.toUpperCase()));

        if (Role.TEACHER.name().equalsIgnoreCase(role)) {
            // user.setStatus("PENDING");  // TODO: Implement pending status
            user.setStatus("ACTIVE"); // to be removed after admin approval functionality is implemented
            if (file != null) {
                String path = fileStorageService.storeFile(file);
                user.setCredentialFilePath(path);
            }
        } else {
            user.setStatus("ACTIVE");
        }

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            
            User user = userRepository.findByEmail(email).orElseThrow();

            return ResponseEntity.ok(Map.of(
                "token", jwt,
                "user", user
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        // Since we disabled security for now (or using basic), this might be null if not authenticated via Spring Security.
        // If we want to test this, we need to pass the user info or enable security.
        // For this phase, let's assume the client sends the email or ID if we are stateless without JWT yet.
        // OR, we can rely on the SecurityContext if we enabled Basic Auth.
        // Given the plan, let's return a placeholder or the principal if available.
        if (authentication != null) {
             return ResponseEntity.ok(authentication.getPrincipal());
        }
        return ResponseEntity.status(401).build();
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Client side logout (clear token)
        return ResponseEntity.ok("Logged out");
    }
}
