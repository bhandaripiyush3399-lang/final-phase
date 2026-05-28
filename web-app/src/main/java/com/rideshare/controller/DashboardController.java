package com.rideshare.controller;

import com.rideshare.model.User;
import com.rideshare.repository.UserRepository;
import com.rideshare.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController extends BaseController {

    public DashboardController(UserRepository userRepository, NotificationService notificationService) {
        super(userRepository, notificationService);
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User user = getCurrentUser(auth);
        model.addAttribute("user", user);
        return "dashboard";
    }
}
