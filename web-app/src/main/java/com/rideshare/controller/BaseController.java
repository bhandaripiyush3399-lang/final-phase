package com.rideshare.controller;

import com.rideshare.model.User;
import com.rideshare.repository.UserRepository;
import com.rideshare.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class BaseController {

    protected final UserRepository userRepository;
    protected final NotificationService notificationService;

    protected BaseController(UserRepository userRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    protected User getCurrentUser(Authentication auth) {
        return userRepository.findByRollNumber(auth.getName())
            .orElseThrow(() -> new IllegalStateException("User not found."));
    }

    @ModelAttribute("unreadCount")
    public long unreadCount(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return 0;
        try {
            User user = getCurrentUser(auth);
            return notificationService.getUnreadCount(user);
        } catch (Exception e) {
            return 0;
        }
    }
}
