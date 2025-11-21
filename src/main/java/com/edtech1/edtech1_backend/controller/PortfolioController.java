package com.edtech1.edtech1_backend.controller;

import com.edtech1.edtech1_backend.model.Document;
import com.edtech1.edtech1_backend.model.User;
import com.edtech1.edtech1_backend.repository.DocumentRepository;
import com.edtech1.edtech1_backend.repository.UserRepository;
import com.edtech1.edtech1_backend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/upload-document")
    public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file, Authentication authentication) {
        // Assuming authentication.getPrincipal() returns User or we can get email
        // For simplicity in this phase, let's assume we can get the user.
        // If using Basic Auth with UserDetails, principal is UserDetails.
        // We need to fetch the User entity.
        
        // Mocking user fetch for now if auth is not fully set up with UserDetails
        // In real app: User user = userService.findByEmail(authentication.getName());
        
        // TEMPORARY: Just find the first student for testing if auth is null/mocked
        User user = userRepository.findAll().stream()
                .filter(u -> u.getRole().name().equals("STUDENT"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No student found"));

        String fileName = fileStorageService.storeFile(file);
        
        Document doc = new Document();
        doc.setFileName(file.getOriginalFilename());
        doc.setFilePath(fileName);
        doc.setFileType(file.getContentType());
        doc.setStudent(user);
        
        documentRepository.save(doc);
        
        return ResponseEntity.ok(doc);
    }

    @GetMapping
    public ResponseEntity<?> getPortfolio(Authentication authentication) {
        // Similar mock user logic
        User user = getUser(authentication);
                
        List<Document> docs = documentRepository.findByStudent(user);
        return ResponseEntity.ok(docs);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long documentId) {
        if (documentId == null) return ResponseEntity.badRequest().body("ID is required");
        documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        documentRepository.deleteById(documentId);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/share/{studentId}")
    public ResponseEntity<?> getSharedPortfolio(@PathVariable Long studentId) {
        List<Document> docs = documentRepository.findByStudentId(studentId);
        return ResponseEntity.ok(docs);
    }

    private User getUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found or unauthorized"));
    }
}
