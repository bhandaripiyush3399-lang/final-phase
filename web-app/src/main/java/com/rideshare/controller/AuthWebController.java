package com.rideshare.controller;

import com.rideshare.model.User;
import com.rideshare.repository.CollegeRepository;
import com.rideshare.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthWebController {

    private final UserRepository userRepository;
    private final CollegeRepository collegeRepository;

    public AuthWebController(UserRepository userRepository, CollegeRepository collegeRepository) {
        this.userRepository = userRepository;
        this.collegeRepository = collegeRepository;
    }

    @GetMapping("/")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) model.addAttribute("error", "Invalid Roll Number or Password.");
        if (logout != null) model.addAttribute("message", "You have been logged out.");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("colleges", collegeRepository.findAllByOrderByCollegeNameAsc());
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String rollNumber,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           @RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam(required = false) String phone,
                           @RequestParam Long collegeId,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        try {
            if (password.length() < 6) throw new IllegalArgumentException("Password must be at least 6 characters.");
            if (!password.equals(confirmPassword)) throw new IllegalArgumentException("Passwords do not match.");
            if (!email.contains("@")) throw new IllegalArgumentException("Valid email is required.");

            String normalized = rollNumber.trim().toUpperCase();
            if (userRepository.existsByRollNumber(normalized)) {
                throw new IllegalArgumentException("Roll Number already registered.");
            }

            var college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid college."));

            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            userRepository.save(new User(normalized, hash, fullName.trim(), email.trim(),
                phone != null ? phone.trim() : "", college));

            redirectAttributes.addFlashAttribute("message", "Registration successful! Please login.");
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("colleges", collegeRepository.findAllByOrderByCollegeNameAsc());
            return "register";
        }
    }
}
