package com.edtech1.edtech1_backend.controller;

import com.edtech1.edtech1_backend.model.User;
import com.edtech1.edtech1_backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<?> register(@RequestParam("name") String name,
                                      @RequestParam("email") String email,
                                      @RequestParam("password") String password,
                                      @RequestParam("role") String role,
                                      @RequestParam(value = "teacherId", required = false) String teacherId,
                                      @RequestParam(value = "schoolName", required = false) String schoolName,
                                      @RequestParam(value = "credentialFile", required = false) MultipartFile credentialFile) {
        try {
            User user = authService.registerUser(name, email, password, role, teacherId, schoolName, credentialFile);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");
            User user = authService.loginUser(email, password);
            // In a real app, generate JWT here. For now returning user details.
            return ResponseEntity.ok(user);
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
