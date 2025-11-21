package com.edtech1.edtech1_backend.repository;

import com.edtech1.edtech1_backend.model.Notification;
import com.edtech1.edtech1_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);
}
