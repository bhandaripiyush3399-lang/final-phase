package com.rideshare.view;

import com.rideshare.controller.RideController;
import com.rideshare.model.Ride.VehicleType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.rideshare.view.ViewHelper.*;

public class OfferRideView {

    private final VBox root;
    private final RideController rideController = new RideController();

    public OfferRideView() {
        root = new VBox(15);
        root.setPadding(new Insets(10));
        root.setMaxWidth(550);

        Label header = createSectionHeader("Offer a Ride");
        Label subtitle = new Label("Share your vehicle with students who missed their bus");
        subtitle.setTextFill(Color.GRAY);

        // Vehicle type
        Label vehicleLabel = new Label("Vehicle Type:");
        ComboBox<String> vehicleCombo = new ComboBox<>();
        vehicleCombo.getItems().addAll("BIKE", "CAR", "LIGHT_VEHICLE");
        vehicleCombo.setPromptText("Select Vehicle Type");
        vehicleCombo.setPrefHeight(40);
        vehicleCombo.setMaxWidth(Double.MAX_VALUE);

        // Route
        TextField fromField = createTextField("Starting Point (e.g., Swargate)");
        TextField toField = createTextField("Destination (e.g., PICT College)");

        // Date and time
        Label dateLabel = new Label("Departure Date:");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefHeight(40);

        Label timeLabel = new Label("Departure Time (HH:MM, 24h format):");
        TextField timeField = createTextField("e.g., 08:30");

        // Seats
        Label seatsLabel = new Label("Available Seats:");
        Spinner<Integer> seatsSpinner = new Spinner<>(1, 6, 1);
        seatsSpinner.setPrefHeight(40);
        seatsSpinner.setEditable(true);

        // Auto-set seats for bike
        vehicleCombo.setOnAction(e -> {
            if ("BIKE".equals(vehicleCombo.getValue())) {
                seatsSpinner.getValueFactory().setValue(1);
                seatsSpinner.setDisable(true);
            } else {
                seatsSpinner.setDisable(false);
            }
        });

        // Fare
        TextField fareField = createTextField("Fare per person (Rs.)");

        Label fareNote = new Label("Note: Fare is negotiable. Payment is cash-based only.");
        fareNote.setTextFill(Color.GRAY);
        fareNote.setStyle("-fx-font-style: italic;");

        Button offerBtn = createPrimaryButton("Offer Ride");
        offerBtn.setOnAction(e -> {
            try {
                String vehicleStr = vehicleCombo.getValue();
                if (vehicleStr == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Please select a vehicle type.");
                    return;
                }

                VehicleType vehicleType = VehicleType.valueOf(vehicleStr);
                String from = fromField.getText();
                String to = toField.getText();

                LocalDate date = datePicker.getValue();
                if (date == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Please select a departure date.");
                    return;
                }

                String timeStr = timeField.getText().trim();
                LocalTime time;
                try {
                    String[] parts = timeStr.split(":");
                    time = LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Error",
                        "Invalid time format. Use HH:MM (e.g., 08:30).");
                    return;
                }

                LocalDateTime departureTime = LocalDateTime.of(date, time);
                int seats = seatsSpinner.getValue();

                double fare;
                try {
                    fare = Double.parseDouble(fareField.getText().trim());
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid fare amount.");
                    return;
                }

                boolean success = rideController.offerRide(vehicleType, from, to,
                                                           departureTime, seats, fare);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Ride offered successfully! Students can now book your ride.");
                    clearForm(vehicleCombo, fromField, toField, timeField, fareField,
                              seatsSpinner, datePicker);
                }
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", ex.getMessage());
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to offer ride: " + ex.getMessage());
            }
        });

        root.getChildren().addAll(
            header, subtitle, new Separator(),
            vehicleLabel, vehicleCombo,
            new Label("Route:"), fromField, toField,
            dateLabel, datePicker,
            timeLabel, timeField,
            seatsLabel, seatsSpinner,
            new Label("Fare (per person):"), fareField, fareNote,
            offerBtn
        );
    }

    public VBox getRoot() {
        return root;
    }

    private void clearForm(ComboBox<String> vehicleCombo, TextField fromField,
                           TextField toField, TextField timeField, TextField fareField,
                           Spinner<Integer> seatsSpinner, DatePicker datePicker) {
        vehicleCombo.setValue(null);
        fromField.clear();
        toField.clear();
        timeField.clear();
        fareField.clear();
        seatsSpinner.getValueFactory().setValue(1);
        seatsSpinner.setDisable(false);
        datePicker.setValue(LocalDate.now());
    }
}
