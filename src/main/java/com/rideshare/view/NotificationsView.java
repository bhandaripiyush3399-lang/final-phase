package com.rideshare.view;

import com.rideshare.controller.NotificationController;
import com.rideshare.model.Notification;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

import static com.rideshare.view.ViewHelper.*;

public class NotificationsView {

    private final VBox root;
    private final NotificationController notifController = new NotificationController();
    private final VBox notifListContainer;

    public NotificationsView() {
        root = new VBox(15);
        root.setPadding(new Insets(10));

        Label header = createSectionHeader("Notifications");
        Label subtitle = new Label("Stay updated on your ride bookings and requests");
        subtitle.setTextFill(Color.GRAY);

        Button markReadBtn = createPrimaryButton("Mark All as Read");
        markReadBtn.setPrefWidth(160);
        markReadBtn.setOnAction(e -> {
            notifController.markAllAsRead();
            loadNotifications();
        });

        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(header, new Region(), markReadBtn);
        HBox.setHgrow(headerBox.getChildren().get(1), Priority.ALWAYS);

        notifListContainer = new VBox(8);
        ScrollPane scrollPane = new ScrollPane(notifListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(headerBox, subtitle, new Separator(), scrollPane);
        loadNotifications();
    }

    public VBox getRoot() {
        return root;
    }

    private void loadNotifications() {
        notifListContainer.getChildren().clear();
        try {
            List<Notification> notifications = notifController.getNotifications();
            if (notifications.isEmpty()) {
                Label noNotifs = new Label("No notifications yet.");
                noNotifs.setFont(Font.font("Segoe UI", 14));
                noNotifs.setTextFill(Color.GRAY);
                noNotifs.setPadding(new Insets(30));
                notifListContainer.getChildren().add(noNotifs);
                return;
            }

            for (Notification notif : notifications) {
                notifListContainer.getChildren().add(createNotifCard(notif));
            }
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error",
                "Failed to load notifications: " + ex.getMessage());
        }
    }

    private HBox createNotifCard(Notification notif) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(12));
        card.setAlignment(Pos.CENTER_LEFT);

        if (!notif.isRead()) {
            card.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 8;");
        } else {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8; "
                + "-fx-border-color: #e0e0e0; -fx-border-radius: 8;");
        }

        // Notification type icon/color
        String typeColor;
        switch (notif.getType()) {
            case BOOKING_REQUEST: typeColor = PRIMARY_COLOR; break;
            case RIDE_ACCEPTED: typeColor = SUCCESS_COLOR; break;
            case RIDE_REJECTED: typeColor = DANGER_COLOR; break;
            case RIDE_CANCELLED: typeColor = DANGER_COLOR; break;
            case BOOKING_CANCELLED: typeColor = WARNING_COLOR; break;
            default: typeColor = "#999"; break;
        }

        Label typeBadge = new Label(notif.getType().name().replace("_", " "));
        typeBadge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        typeBadge.setTextFill(Color.WHITE);
        typeBadge.setPadding(new Insets(2, 8, 2, 8));
        typeBadge.setStyle("-fx-background-color: " + typeColor + "; -fx-background-radius: 10;");

        VBox content = new VBox(4);
        Label messageLabel = new Label(notif.getMessage());
        messageLabel.setWrapText(true);
        messageLabel.setFont(Font.font("Segoe UI", notif.isRead() ? FontWeight.NORMAL : FontWeight.BOLD, 13));

        Label timeLabel = new Label(notif.getCreatedAt().toString());
        timeLabel.setFont(Font.font("Segoe UI", 10));
        timeLabel.setTextFill(Color.GRAY);

        content.getChildren().addAll(typeBadge, messageLabel, timeLabel);
        HBox.setHgrow(content, Priority.ALWAYS);

        card.getChildren().add(content);
        return card;
    }
}
