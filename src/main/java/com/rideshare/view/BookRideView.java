package com.rideshare.view;

import com.rideshare.controller.RideController;
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

public class BookRideView {

    private final VBox root;
    private final RideController rideController = new RideController();
    private final VBox rideListContainer;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public BookRideView() {
        root = new VBox(15);
        root.setPadding(new Insets(10));

        Label header = createSectionHeader("Available Rides");
        Label subtitle = new Label("Find and book rides from fellow students");
        subtitle.setTextFill(Color.GRAY);

        Button refreshBtn = createPrimaryButton("Refresh");
        refreshBtn.setPrefWidth(120);

        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(header, new Region(), refreshBtn);
        HBox.setHgrow(headerBox.getChildren().get(1), Priority.ALWAYS);

        rideListContainer = new VBox(10);
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
            List<Ride> rides = rideController.getAvailableRides();
            if (rides.isEmpty()) {
                Label noRides = new Label("No rides available at the moment. Check back later!");
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
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(15));

        // Top row: route and vehicle type
        Label routeLabel = new Label(ride.getRouteFrom() + "  ->  " + ride.getRouteTo());
        routeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        HBox vehicleBadge = createStatusBadge(
            ride.getVehicleType().name().replace("_", " "),
            ride.getVehicleType() == Ride.VehicleType.BIKE ? WARNING_COLOR : PRIMARY_COLOR
        );

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.getChildren().addAll(routeLabel, vehicleBadge);

        // Details
        Label providerLabel = new Label("Offered by: " + ride.getProviderName()
            + " (" + ride.getProviderCollege() + ")");
        providerLabel.setTextFill(Color.web("#555"));

        Label timeLabel = new Label("Departure: " + ride.getDepartureTime().format(FORMATTER));
        Label seatsLabel = new Label("Seats Available: " + ride.getAvailableSeats()
            + " / " + ride.getTotalSeats());
        Label fareLabel = new Label("Fare: Rs. " + String.format("%.2f", ride.getFare())
            + " (Cash, Negotiable)");
        fareLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        fareLabel.setTextFill(Color.web(SUCCESS_COLOR));

        // Book button
        Button bookBtn = createSuccessButton("Book This Ride");
        bookBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Book ride from " + ride.getRouteFrom() + " to " + ride.getRouteTo()
                + "?\nFare: Rs. " + String.format("%.2f", ride.getFare()) + " (Cash)",
                ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirm Booking");
            confirm.setHeaderText(null);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    handleBookRide(ride.getRideId());
                }
            });
        });

        card.getChildren().addAll(topRow, providerLabel, timeLabel, seatsLabel, fareLabel, bookBtn);
        return card;
    }

    private void handleBookRide(int rideId) {
        try {
            boolean success = rideController.bookRide(rideId);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Booking request sent! The ride provider will be notified.");
                loadRides();
            }
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.WARNING, "Cannot Book", ex.getMessage());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Booking failed: " + ex.getMessage());
        }
    }
}
