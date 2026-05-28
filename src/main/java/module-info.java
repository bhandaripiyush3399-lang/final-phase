module com.rideshare {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.mail;
    requires jbcrypt;

    opens com.rideshare to javafx.fxml;
    opens com.rideshare.controller to javafx.fxml;
    opens com.rideshare.view to javafx.fxml;
    opens com.rideshare.model to javafx.base;

    exports com.rideshare;
    exports com.rideshare.controller;
    exports com.rideshare.view;
    exports com.rideshare.model;
    exports com.rideshare.dao;
}
