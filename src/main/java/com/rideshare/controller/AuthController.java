package com.rideshare.controller;

import com.rideshare.dao.UserDAO;
import com.rideshare.model.User;

public class AuthController {

    private final UserDAO userDAO = new UserDAO();

    public User login(String rollNumber, String password) {
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Roll Number is required.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }

        User user = userDAO.authenticate(rollNumber.trim().toUpperCase(), password);
        if (user != null) {
            SessionManager.getInstance().setCurrentUser(user);
        }
        return user;
    }

    public boolean register(String rollNumber, String password, String fullName,
                            String email, String phone, int collegeId) {
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Roll Number is required.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full Name is required.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Valid email is required.");
        }
        if (collegeId <= 0) {
            throw new IllegalArgumentException("Please select your college.");
        }

        String normalizedRoll = rollNumber.trim().toUpperCase();
        if (userDAO.rollNumberExists(normalizedRoll)) {
            throw new IllegalArgumentException("Roll Number already registered.");
        }

        User user = new User(normalizedRoll, password, fullName.trim(),
                             email.trim(), phone != null ? phone.trim() : "", collegeId);
        return userDAO.registerUser(user);
    }

    public void logout() {
        SessionManager.getInstance().logout();
    }
}
