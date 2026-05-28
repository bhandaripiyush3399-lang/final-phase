package com.rideshare.view;

import com.rideshare.controller.RideController;
import com.rideshare.model.Booking;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

import static com.rideshare.view.ViewHelper.*;

public class MyBookingsView {

    private final VBox root;
    private final RideController rideController = new RideController();
    private final VBox bookingListContainer;

    public MyBookingsView() {
        root = new VBox(15);
        root.setPadding(new Insets(10));

        Label header = createSectionHeader("My Bookings");
        Label subtitle = new Label("Track your ride booking requests");
        subtitle.setTextFill(Color.GRAY);

        Button refreshBtn = createPrimaryButton("Refresh");
        refreshBtn.setPrefWidth(120);

        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(header, new Region(), refreshBtn);
        HBox.setHgrow(headerBox.getChildren().get(1), Priority.ALWAYS);

        bookingListContainer = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(bookingListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        refreshBtn.setOnAction(e -> loadBookings());

        root.getChildren().addAll(headerBox, subtitle, new Separator(), scrollPane);
        loadBookings();
    }

    public VBox getRoot() {
        return root;
    }

    private void loadBookings() {
        bookingListContainer.getChildren().clear();
        try {
            List<Booking> bookings = rideController.getMyBookings();
            if (bookings.isEmpty()) {
                Label noBookings = new Label("You haven't booked any rides yet. Go to 'Book a Ride' to find available rides!");
                noBookings.setFont(Font.font("Segoe UI", 14));
                noBookings.setTextFill(Color.GRAY);
                noBookings.setPadding(new Insets(30));
                bookingListContainer.getChildren().add(noBookings);
                return;
            }

            for (Booking booking : bookings) {
                bookingListContainer.getChildren().add(createBookingCard(booking));
            }
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error",
                "Failed to load bookings: " + ex.getMessage());
        }
    }

    private VBox createBookingCard(Booking booking) {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(15));

        // Route
        Label routeLabel = new Label(booking.getRouteFrom() + "  ->  " + booking.getRouteTo());
        routeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));

        // Status badge
        String statusColor;
        switch (booking.getStatus()) {
            case PENDING: statusColor = WARNING_COLOR; break;
            case ACCEPTED: statusColor = SUCCESS_COLOR; break;
            case REJECTED: statusColor = DANGER_COLOR; break;
            default: statusColor = "#999"; break;
        }
        HBox badge = createStatusBadge(booking.getStatus().name(), statusColor);

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.getChildren().addAll(routeLabel, badge);

        // Details
        Label providerLabel = new Label("Ride Provider: " + booking.getProviderName());
        Label vehicleLabel = new Label("Vehicle: "
            + booking.getVehicleType().replace("_", " "));
        Label fareLabel = new Label("Fare: Rs. " + String.format("%.2f", booking.getFare())
            + " (Cash, Negotiable)");
        fareLabel.setTextFill(Color.web(SUCCESS_COLOR));

        Label timeLabel = new Label("Booked on: " + booking.getBookingTime());

        card.getChildren().addAll(topRow, providerLabel, vehicleLabel, fareLabel, timeLabel);

        // Cancel button for pending/accepted bookings
        if (booking.getStatus() == Booking.BookingStatus.PENDING
            || booking.getStatus() == Booking.BookingStatus.ACCEPTED) {

            Button cancelBtn = createDangerButton("Cancel My Booking");
            cancelBtn.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Cancel your booking? The ride provider will be notified.",
                    ButtonType.YES, ButtonType.NO);
                confirm.setTitle("Cancel Booking");
                confirm.setHeaderText(null);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        try {
                            rideController.cancelBooking(
                                booking.getBookingId(), booking.getRideId());
                            showAlert(Alert.AlertType.INFORMATION, "Cancelled",
                                "Your booking has been cancelled.");
                            loadBookings();
                        } catch (Exception ex) {
                            showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
                        }
                    }
                });
            });
            card.getChildren().add(cancelBtn);
        }

        return card;
    }
}
