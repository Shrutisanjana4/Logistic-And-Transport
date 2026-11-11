package com.primeroute.logistics.repository;

import com.primeroute.logistics.entity.Notification;
import com.primeroute.logistics.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    List<Notification> findTop10ByUserOrderByCreatedAtDesc(User user);

    long countByUserAndIsReadFalse(User user);

    boolean existsByUserAndTitleAndTypeAndCreatedAtAfter(User user, String title, String type, LocalDateTime after);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user = :user AND n.isRead = false")
    void markAllAsReadForUser(@Param("user") User user);
}
