package com.rideshare.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Utility class for building consistent UI components.
 */
public class ViewHelper {

    public static final String PRIMARY_COLOR = "#2196F3";
    public static final String SUCCESS_COLOR = "#4CAF50";
    public static final String DANGER_COLOR = "#f44336";
    public static final String WARNING_COLOR = "#FF9800";
    public static final String BG_COLOR = "#f5f5f5";

    public static Label createTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        label.setTextFill(Color.web(PRIMARY_COLOR));
        return label;
    }

    public static Label createSubtitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        label.setTextFill(Color.GRAY);
        return label;
    }

    public static Label createSectionHeader(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        label.setTextFill(Color.web("#333333"));
        return label;
    }

    public static TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("custom-text-field");
        field.setPrefHeight(40);
        return field;
    }

    public static PasswordField createPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.getStyleClass().add("custom-text-field");
        field.setPrefHeight(40);
        return field;
    }

    public static Button createPrimaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("primary-button");
        button.setPrefHeight(40);
        button.setPrefWidth(200);
        return button;
    }

    public static Button createSuccessButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("success-button");
        button.setPrefHeight(35);
        return button;
    }

    public static Button createDangerButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("danger-button");
        button.setPrefHeight(35);
        return button;
    }

    public static Button createNavButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        button.setPrefHeight(45);
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    public static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static VBox createCard(String title, String content) {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(15));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);

        card.getChildren().addAll(titleLabel, contentLabel);
        return card;
    }

    public static HBox createStatusBadge(String text, String color) {
        Label badge = new Label(text);
        badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        badge.setTextFill(Color.WHITE);
        badge.setPadding(new Insets(3, 10, 3, 10));
        badge.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 12;");

        HBox container = new HBox(badge);
        container.setAlignment(Pos.CENTER_LEFT);
        return container;
    }
}
