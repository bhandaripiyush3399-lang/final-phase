package com.rideshare.view;

import com.rideshare.controller.AuthController;
import com.rideshare.controller.NotificationController;
import com.rideshare.controller.SessionManager;
import com.rideshare.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static com.rideshare.view.ViewHelper.*;

public class DashboardView {

    private final BorderPane root;
    private final StackPane contentArea;
    private final NotificationController notifController = new NotificationController();
    private Button activeNavButton;

    public DashboardView() {
        root = new BorderPane();
        root.getStyleClass().add("root-pane");

        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));

        root.setTop(createTopBar());
        root.setLeft(createSidebar());
        root.setCenter(contentArea);

        showDashboardHome();
    }

    public BorderPane getRoot() {
        return root;
    }

    private HBox createTopBar() {
        User user = SessionManager.getInstance().getCurrentUser();

        HBox topBar = new HBox();
        topBar.getStyleClass().add("top-bar");
        topBar.setPadding(new Insets(12, 20, 12, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(15);

        Label appTitle = new Label("College Ride Share");
        appTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        appTitle.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("Welcome, " + user.getFullName());
        userLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        userLabel.setTextFill(Color.WHITE);

        Label collegeLabel = new Label("| " + user.getCollegeName());
        collegeLabel.setFont(Font.font("Segoe UI", 12));
        collegeLabel.setTextFill(Color.web("#bbdefb"));

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("danger-button");
        logoutBtn.setOnAction(e -> handleLogout());

        topBar.getChildren().addAll(appTitle, spacer, userLabel, collegeLabel, logoutBtn);
        return topBar;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(5);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(15, 10, 15, 10));
        sidebar.setPrefWidth(200);

        Button dashBtn = createNavButton("Dashboard");
        Button bookBtn = createNavButton("Book a Ride");
        Button offerBtn = createNavButton("Offer a Ride");
        Button myRidesBtn = createNavButton("My Offered Rides");
        Button myBookingsBtn = createNavButton("My Bookings");
        Button notifsBtn = createNavButton("Notifications");
        Button emergencyBtn = createNavButton("Emergency Contacts");

        // Update notification badge
        int unread = notifController.getUnreadCount();
        if (unread > 0) {
            notifsBtn.setText("Notifications (" + unread + ")");
        }

        dashBtn.setOnAction(e -> { setActiveNav(dashBtn); showDashboardHome(); });
        bookBtn.setOnAction(e -> { setActiveNav(bookBtn); showBookRide(); });
        offerBtn.setOnAction(e -> { setActiveNav(offerBtn); showOfferRide(); });
        myRidesBtn.setOnAction(e -> { setActiveNav(myRidesBtn); showMyRides(); });
        myBookingsBtn.setOnAction(e -> { setActiveNav(myBookingsBtn); showMyBookings(); });
        notifsBtn.setOnAction(e -> { setActiveNav(notifsBtn); showNotifications(); });
        emergencyBtn.setOnAction(e -> { setActiveNav(emergencyBtn); showEmergencyContacts(); });

        sidebar.getChildren().addAll(
            dashBtn, new Separator(),
            bookBtn, offerBtn, new Separator(),
            myRidesBtn, myBookingsBtn, new Separator(),
            notifsBtn, emergencyBtn
        );

        setActiveNav(dashBtn);
        return sidebar;
    }

    private void setActiveNav(Button btn) {
        if (activeNavButton != null) {
            activeNavButton.getStyleClass().remove("nav-button-active");
        }
        btn.getStyleClass().add("nav-button-active");
        activeNavButton = btn;
    }

    private void showDashboardHome() {
        contentArea.getChildren().clear();
        User user = SessionManager.getInstance().getCurrentUser();

        VBox content = new VBox(20);
        content.setPadding(new Insets(10));

        Label welcome = createSectionHeader("Welcome, " + user.getFullName() + "!");

        Label info = new Label(
            "Missed your college bus? No worries!\n\n" +
            "Use this system to:\n" +
            "  - Book a Ride: Find students offering rides and request a seat\n" +
            "  - Offer a Ride: Share your vehicle with fellow students\n\n" +
            "Payment is cash-based and negotiated between riders.\n" +
            "Select an option from the sidebar to get started."
        );
        info.setWrapText(true);
        info.setFont(Font.font("Segoe UI", 14));

        HBox stats = new HBox(20);
        stats.getChildren().addAll(
            createCard("Your Roll Number", user.getRollNumber()),
            createCard("College", user.getCollegeName()),
            createCard("Email", user.getEmail())
        );

        content.getChildren().addAll(welcome, info, stats);
        contentArea.getChildren().add(content);
    }

    private void showBookRide() {
        contentArea.getChildren().clear();
        BookRideView bookView = new BookRideView();
        contentArea.getChildren().add(bookView.getRoot());
    }

    private void showOfferRide() {
        contentArea.getChildren().clear();
        OfferRideView offerView = new OfferRideView();
        contentArea.getChildren().add(offerView.getRoot());
    }

    private void showMyRides() {
        contentArea.getChildren().clear();
        MyRidesView myRidesView = new MyRidesView();
        contentArea.getChildren().add(myRidesView.getRoot());
    }

    private void showMyBookings() {
        contentArea.getChildren().clear();
        MyBookingsView myBookingsView = new MyBookingsView();
        contentArea.getChildren().add(myBookingsView.getRoot());
    }

    private void showNotifications() {
        contentArea.getChildren().clear();
        NotificationsView notifsView = new NotificationsView();
        contentArea.getChildren().add(notifsView.getRoot());
    }

    private void showEmergencyContacts() {
        contentArea.getChildren().clear();
        EmergencyContactsView emergencyView = new EmergencyContactsView();
        contentArea.getChildren().add(emergencyView.getRoot());
    }

    private void handleLogout() {
        new AuthController().logout();
        LoginView loginView = new LoginView();
        Scene scene = root.getScene();
        scene.setRoot(loginView.getRoot());
    }
}
