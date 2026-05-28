package com.rideshare.controller;

import com.rideshare.model.User;
import com.rideshare.repository.CollegeRepository;
import com.rideshare.repository.UserRepository;
import com.rideshare.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class NotificationWebController extends BaseController {

    private final CollegeRepository collegeRepository;

    public NotificationWebController(UserRepository userRepository,
                                     NotificationService notificationService,
                                     CollegeRepository collegeRepository) {
        super(userRepository, notificationService);
        this.collegeRepository = collegeRepository;
    }

    @GetMapping("/notifications")
    public String notifications(Authentication auth, Model model) {
        User user = getCurrentUser(auth);
        model.addAttribute("notifications", notificationService.getNotifications(user));
        model.addAttribute("user", user);
        return "notifications";
    }

    @PostMapping("/notifications/read")
    public String markAllRead(Authentication auth, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(auth);
        notificationService.markAllAsRead(user);
        redirectAttributes.addFlashAttribute("success", "All notifications marked as read.");
        return "redirect:/notifications";
    }

    @GetMapping("/emergency")
    public String emergencyContacts(Model model) {
        model.addAttribute("colleges", collegeRepository.findAllByOrderByCollegeNameAsc());
        return "emergency";
    }
}
