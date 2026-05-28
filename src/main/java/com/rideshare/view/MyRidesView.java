package com.rideshare.view;

import com.rideshare.controller.RideController;
import com.rideshare.model.Booking;
import com.rideshare.model.Ride;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.rideshare.view.ViewHelper.*;

public class MyRidesView {

    private final VBox root;
    private final RideController rideController = new RideController();
    private final VBox rideListContainer;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public MyRidesView() {
        root = new VBox(15);
        root.setPadding(new Insets(10));

        Label header = createSectionHeader("My Offered Rides");
        Label subtitle = new Label("Manage rides you have offered and handle booking requests");
        subtitle.setTextFill(Color.GRAY);

        Button refreshBtn = createPrimaryButton("Refresh");
        refreshBtn.setPrefWidth(120);

        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(header, new Region(), refreshBtn);
        HBox.setHgrow(headerBox.getChildren().get(1), Priority.ALWAYS);

        rideListContainer = new VBox(15);
        ScrollPane scrollPane = new ScrollPane(rideListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        refreshBtn.setOnAction(e -> loadRides());

        root.getChildren().addAll(headerBox, subtitle, new Separator(), scrollPane);
        loadRides();
    }

    public VBox getRoot() {
        return root;
    }

    private void loadRides() {
        rideListContainer.getChildren().clear();
        try {
            List<Ride> rides = rideController.getMyOfferedRides();
            if (rides.isEmpty()) {
                Label noRides = new Label("You haven't offered any rides yet. Go to 'Offer a Ride' to get started!");
                noRides.setFont(Font.font("Segoe UI", 14));
                noRides.setTextFill(Color.GRAY);
                noRides.setPadding(new Insets(30));
                rideListContainer.getChildren().add(noRides);
                return;
            }

            for (Ride ride : rides) {
                rideListContainer.getChildren().add(createRideCard(ride));
            }
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load rides: " + ex.getMessage());
        }
    }

    private VBox createRideCard(Ride ride) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(15));

        // Route and status
        Label routeLabel = new Label(ride.getRouteFrom() + "  ->  " + ride.getRouteTo());
        routeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        String statusColor;
        switch (ride.getStatus()) {
            case ACTIVE: statusColor = SUCCESS_COLOR; break;
            case FULL: statusColor = WARNING_COLOR; break;
            case CANCELLED: statusColor = DANGER_COLOR; break;
            default: statusColor = "#999"; break;
        }
        HBox statusBadge = createStatusBadge(ride.getStatus().name(), statusColor);

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.getChildren().addAll(routeLabel, statusBadge);

        Label details = new Label(
            "Vehicle: " + ride.getVehicleType().name().replace("_", " ")
            + "  |  Departure: " + ride.getDepartureTime().format(FORMATTER)
            + "  |  Seats: " + ride.getAvailableSeats() + "/" + ride.getTotalSeats()
            + "  |  Fare: Rs. " + String.format("%.2f", ride.getFare())
        );
        details.setTextFill(Color.web("#555"));

        card.getChildren().addAll(topRow, details);

        // Cancel button (only for active rides)
        if (ride.getStatus() == Ride.RideStatus.ACTIVE || ride.getStatus() == Ride.RideStatus.FULL) {
            Button cancelBtn = createDangerButton("Cancel This Ride");
            cancelBtn.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Cancel this ride? All existing bookings will be cancelled and passengers notified.",
                    ButtonType.YES, ButtonType.NO);
                confirm.setTitle("Cancel Ride");
                confirm.setHeaderText(null);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        try {
                            rideController.cancelRide(ride.getRideId());
                            showAlert(Alert.AlertType.INFORMATION, "Cancelled",
                                "Ride cancelled. All passengers have been notified.");
                            loadRides();
                        } catch (Exception ex) {
                            showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
                        }
                    }
                });
            });
            card.getChildren().add(cancelBtn);
        }

        // Show booking requests
        List<Booking> bookings = rideController.getBookingsForRide(ride.getRideId());
        if (!bookings.isEmpty()) {
            Label bookingHeader = new Label("Booking Requests:");
            bookingHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            bookingHeader.setPadding(new Insets(5, 0, 0, 0));
            card.getChildren().add(bookingHeader);

            for (Booking booking : bookings) {
                card.getChildren().add(createBookingRow(booking, ride));
            }
        }

        return card;
    }

    private HBox createBookingRow(Booking booking, Ride ride) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5, 0, 5, 15));
        row.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

        Label nameLabel = new Label(booking.getSeekerName()
            + " (" + booking.getSeekerCollege() + ")"
            + " - Phone: " + booking.getSeekerPhone());
        nameLabel.setFont(Font.font("Segoe UI", 12));

        String badgeColor;
        switch (booking.getStatus()) {
            case PENDING: badgeColor = WARNING_COLOR; break;
            case ACCEPTED: badgeColor = SUCCESS_COLOR; break;
            case REJECTED: badgeColor = DANGER_COLOR; break;
            default: badgeColor = "#999"; break;
        }
        HBox badge = createStatusBadge(booking.getStatus().name(), badgeColor);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(nameLabel, badge, spacer);

        if (booking.getStatus() == Booking.BookingStatus.PENDING
            && ride.getStatus() != Ride.RideStatus.CANCELLED) {
            Button acceptBtn = createSuccessButton("Accept");
            acceptBtn.setPrefWidth(80);
            Button rejectBtn = createDangerButton("Reject");
            rejectBtn.setPrefWidth(80);

            acceptBtn.setOnAction(e -> {
                rideController.acceptBooking(booking.getBookingId(), ride.getRideId());
                showAlert(Alert.AlertType.INFORMATION, "Accepted",
                    booking.getSeekerName() + "'s booking has been accepted.");
                loadRides();
            });

            rejectBtn.setOnAction(e -> {
                rideController.rejectBooking(booking.getBookingId(), ride.getRideId());
                showAlert(Alert.AlertType.INFORMATION, "Rejected",
                    booking.getSeekerName() + "'s booking has been rejected.");
                loadRides();
            });

            row.getChildren().addAll(acceptBtn, rejectBtn);
        }

        return row;
    }
}
