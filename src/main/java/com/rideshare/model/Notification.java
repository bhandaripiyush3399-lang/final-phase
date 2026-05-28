package com.rideshare.model;

import java.sql.Timestamp;

public class Notification {

    public enum NotificationType {
        BOOKING_REQUEST, RIDE_ACCEPTED, RIDE_REJECTED,
        RIDE_CANCELLED, BOOKING_CANCELLED, GENERAL
    }

    private int notificationId;
    private int userId;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private Timestamp createdAt;

    public Notification() {}

    public Notification(int userId, String message, NotificationType type) {
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.isRead = false;
    }

    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
