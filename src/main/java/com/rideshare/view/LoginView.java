package com.rideshare.view;

import com.rideshare.controller.AuthController;
import com.rideshare.dao.CollegeDAO;
import com.rideshare.model.College;
import com.rideshare.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;

import static com.rideshare.view.ViewHelper.*;

public class LoginView {

    private final BorderPane root;
    private final AuthController authController = new AuthController();

    public LoginView() {
        root = new BorderPane();
        root.getStyleClass().add("root-pane");
        showLoginForm();
    }

    public BorderPane getRoot() {
        return root;
    }

    private void showLoginForm() {
        VBox loginBox = new VBox(20);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(40));
        loginBox.setMaxWidth(420);

        // Header
        Label titleLabel = createTitle("College Ride Share");
        Label subtitleLabel = createSubtitle("Connect with fellow students for rides");

        // Icon placeholder
        Text icon = new Text("\uD83D\uDE97");
        icon.setFont(Font.font(50));

        // Form
        TextField rollNumberField = createTextField("Roll Number (e.g., PICT001)");
        PasswordField passwordField = createPasswordField("Password");

        Button loginButton = createPrimaryButton("Login");
        loginButton.setOnAction(e -> handleLogin(rollNumberField.getText(), passwordField.getText()));

        // Enter key support
        passwordField.setOnAction(e -> handleLogin(rollNumberField.getText(), passwordField.getText()));

        Hyperlink registerLink = new Hyperlink("New student? Register here");
        registerLink.getStyleClass().add("link");
        registerLink.setOnAction(e -> showRegistrationForm());

        Separator separator = new Separator();
        separator.setPadding(new Insets(5, 0, 5, 0));

        // Emergency contacts info
        Label emergencyLabel = new Label("For emergencies, contact your college office");
        emergencyLabel.setFont(Font.font("Segoe UI", 11));
        emergencyLabel.setTextFill(Color.GRAY);

        loginBox.getChildren().addAll(
            icon, titleLabel, subtitleLabel,
            new Separator(),
            rollNumberField, passwordField,
            loginButton, registerLink,
            separator, emergencyLabel
        );

        VBox wrapper = new VBox(loginBox);
        wrapper.setAlignment(Pos.CENTER);
        root.setCenter(wrapper);
    }

    private void showRegistrationForm() {
        VBox regBox = new VBox(15);
        regBox.setAlignment(Pos.CENTER);
        regBox.setPadding(new Insets(30));
        regBox.setMaxWidth(450);

        Label titleLabel = createTitle("Student Registration");
        Label subtitleLabel = createSubtitle("Only registered college students can access this system");

        TextField rollField = createTextField("Roll Number");
        TextField nameField = createTextField("Full Name");
        TextField emailField = createTextField("Email Address");
        TextField phoneField = createTextField("Phone Number (optional)");
        PasswordField passField = createPasswordField("Password (min 6 chars)");
        PasswordField confirmField = createPasswordField("Confirm Password");

        ComboBox<College> collegeCombo = new ComboBox<>();
        collegeCombo.setPromptText("Select Your College");
        collegeCombo.setPrefHeight(40);
        collegeCombo.setMaxWidth(Double.MAX_VALUE);

        try {
            List<College> colleges = new CollegeDAO().getAllColleges();
            collegeCombo.getItems().addAll(colleges);
        } catch (Exception ex) {
            showAlert(Alert.AlertType.WARNING, "Warning",
                "Could not load colleges. Please check database connection.");
        }

        Button registerButton = createPrimaryButton("Register");
        registerButton.setOnAction(e -> {
            if (!passField.getText().equals(confirmField.getText())) {
                showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match.");
                return;
            }
            College selectedCollege = collegeCombo.getValue();
            if (selectedCollege == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select your college.");
                return;
            }
            handleRegister(rollField.getText(), passField.getText(), nameField.getText(),
                           emailField.getText(), phoneField.getText(), selectedCollege.getCollegeId());
        });

        Hyperlink backLink = new Hyperlink("Already registered? Login here");
        backLink.getStyleClass().add("link");
        backLink.setOnAction(e -> showLoginForm());

        regBox.getChildren().addAll(
            titleLabel, subtitleLabel,
            new Separator(),
            rollField, nameField, emailField, phoneField,
            passField, confirmField, collegeCombo,
            registerButton, backLink
        );

        ScrollPane scrollPane = new ScrollPane(regBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox wrapper = new VBox(scrollPane);
        wrapper.setAlignment(Pos.CENTER);
        root.setCenter(wrapper);
    }

    private void handleLogin(String rollNumber, String password) {
        try {
            User user = authController.login(rollNumber, password);
            if (user != null) {
                navigateToDashboard();
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed",
                    "Invalid Roll Number or Password. Only registered students can access.");
            }
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.ERROR, "Login Error", ex.getMessage());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Connection Error",
                "Could not connect to database. Please ensure MySQL is running.\n\n" + ex.getMessage());
        }
    }

    private void handleRegister(String rollNumber, String password, String fullName,
                                String email, String phone, int collegeId) {
        try {
            boolean success = authController.register(rollNumber, password, fullName,
                                                      email, phone, collegeId);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Registration successful! You can now login.");
                showLoginForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                    "Registration failed. Please try again.");
            }
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", ex.getMessage());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error",
                "Registration failed: " + ex.getMessage());
        }
    }

    private void navigateToDashboard() {
        DashboardView dashboardView = new DashboardView();
        Scene scene = root.getScene();
        scene.setRoot(dashboardView.getRoot());
    }
}
