package com.edtech1.edtech1_backend.repository;

import com.edtech1.edtech1_backend.model.Document;
import com.edtech1.edtech1_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByStudent(User student);
    List<Document> findByStudentId(Long studentId);
}
