package com.rideshare.controller;

import com.rideshare.dao.NotificationDAO;
import com.rideshare.model.Notification;
import com.rideshare.model.User;

import java.util.List;

public class NotificationController {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    public List<Notification> getNotifications() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("Not logged in.");
        }
        return notificationDAO.getNotificationsForUser(currentUser.getUserId());
    }

    public int getUnreadCount() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            return 0;
        }
        return notificationDAO.getUnreadCount(currentUser.getUserId());
    }

    public void markAllAsRead() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            notificationDAO.markAllAsRead(currentUser.getUserId());
        }
    }
}
