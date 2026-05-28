package com.rideshare.view;

import com.rideshare.dao.CollegeDAO;
import com.rideshare.model.College;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

import static com.rideshare.view.ViewHelper.*;

public class EmergencyContactsView {

    private final VBox root;

    public EmergencyContactsView() {
        root = new VBox(15);
        root.setPadding(new Insets(10));

        Label header = createSectionHeader("Emergency Contacts");
        Label subtitle = new Label("College contact numbers for emergencies");
        subtitle.setTextFill(Color.GRAY);

        Label warning = new Label(
            "If you feel unsafe during a ride, contact your college immediately.");
        warning.setTextFill(Color.web(DANGER_COLOR));
        warning.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

        root.getChildren().addAll(header, subtitle, warning, new Separator());

        try {
            List<College> colleges = new CollegeDAO().getAllColleges();
            for (College college : colleges) {
                root.getChildren().add(createCollegeCard(college));
            }
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error",
                "Failed to load contacts: " + ex.getMessage());
        }
    }

    public VBox getRoot() {
        return root;
    }

    private VBox createCollegeCard(College college) {
        VBox card = new VBox(5);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(12));

        Label nameLabel = new Label(college.getCollegeName() + " (" + college.getCollegeCode() + ")");
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        Label addressLabel = new Label("Address: " + college.getAddress());
        addressLabel.setTextFill(Color.web("#555"));

        Label phoneLabel = new Label("Emergency Contact: " + college.getEmergencyContact());
        phoneLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        phoneLabel.setTextFill(Color.web(DANGER_COLOR));

        card.getChildren().addAll(nameLabel, addressLabel, phoneLabel);
        return card;
    }
}
