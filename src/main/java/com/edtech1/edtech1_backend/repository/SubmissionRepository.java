package com.edtech1.edtech1_backend.repository;

import com.edtech1.edtech1_backend.model.Submission;
import com.edtech1.edtech1_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByStudent(User student);
    List<Submission> findByAssignmentId(Long assignmentId);
    long countByStudentAndGradeIsNull(User student);
    long countByStudentAndGradeIsNotNull(User student);
    
    // For Teacher Dashboard
    List<Submission> findTop5ByAssignmentTeacherOrderBySubmittedAtDesc(User teacher);
    List<Submission> findByAssignmentTeacher(User teacher);
    
    // For Student Dashboard (Average Grade)
    List<Submission> findByStudentAndGradeIsNotNull(User student);
}
