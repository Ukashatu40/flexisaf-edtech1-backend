package com.edtech1.edtech1_backend.repository;

import com.edtech1.edtech1_backend.model.Classroom;
import com.edtech1.edtech1_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    List<Classroom> findByTeacher(User teacher);
}
