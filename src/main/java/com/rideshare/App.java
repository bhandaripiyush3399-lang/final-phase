package com.rideshare;

import com.rideshare.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main entry point for the College Ride Share application.
 */
public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("College Ride Share System");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);

        LoginView loginView = new LoginView();
        Scene scene = new Scene(loginView.getRoot(), 900, 650);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
