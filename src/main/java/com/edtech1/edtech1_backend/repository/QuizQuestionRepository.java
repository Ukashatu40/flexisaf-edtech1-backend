package com.edtech1.edtech1_backend.repository;

import com.edtech1.edtech1_backend.model.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
}
