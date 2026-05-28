package com.rideshare.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "notifications")
public class Notification {

    public enum NotificationType {
        BOOKING_REQUEST, RIDE_ACCEPTED, RIDE_REJECTED,
        RIDE_CANCELLED, BOOKING_CANCELLED, GENERAL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type = NotificationType.GENERAL;

    private boolean isRead = false;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Notification() {}

    public Notification(User user, String message, NotificationType type) {
        this.user = user;
        this.message = message;
        this.type = type;
    }

    public Long getNotificationId() { return notificationId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getTypeDisplayName() { return type.name().replace("_", " "); }

    public String getFormattedTime() {
        return createdAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
    }

    public String getTypeBadgeClass() {
        return switch (type) {
            case BOOKING_REQUEST -> "bg-primary";
            case RIDE_ACCEPTED -> "bg-success";
            case RIDE_REJECTED, RIDE_CANCELLED -> "bg-danger";
            case BOOKING_CANCELLED -> "bg-warning text-dark";
            default -> "bg-secondary";
        };
    }
}
