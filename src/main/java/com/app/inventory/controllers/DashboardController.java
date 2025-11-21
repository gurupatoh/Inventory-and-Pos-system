package com.app.inventory.controllers;

import com.app.inventory.auth.SessionManager;
import com.app.inventory.models.User;
import com.app.inventory.models.Role;
import com.app.inventory.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private VBox adminMenu; // Admin-only section

    @FXML
    private VBox staffMenu; // Staff & Admin section

    @FXML
    private Button logoutButton;

    @FXML
    private VBox mainContent; // Dynamic content area

    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
            setupMenuAccess();
        }

        // Optional: load default dashboard content
        loadDashboardContent();
    }

    private void setupMenuAccess() {
        if (currentUser.getRole() == Role.ADMIN) {
            if (adminMenu != null) adminMenu.setVisible(true);
            if (staffMenu != null) staffMenu.setVisible(true);
        } else if (currentUser.getRole() == Role.STAFF) {
            if (adminMenu != null) adminMenu.setVisible(false);
            if (staffMenu != null) staffMenu.setVisible(true);
        } else {
            if (adminMenu != null) adminMenu.setVisible(false);
            if (staffMenu != null) staffMenu.setVisible(false);
        }
    }

    private void loadDashboardContent() {
        if (mainContent != null) {
            mainContent.getChildren().clear();
            Label defaultLabel = new Label("Dashboard content goes here.");
            defaultLabel.setStyle("-fx-font-size: 16px;");
            mainContent.getChildren().add(defaultLabel);
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.clearSession();
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        SceneSwitcher.switchTo(stage, "login.fxml");
    }

    // Example buttons for side menus
    @FXML
    private void openInventory() {
        if (mainContent != null) {
            mainContent.getChildren().clear();
            mainContent.getChildren().add(new Label("Inventory Section Loaded"));
        }
    }

    @FXML
    private void openOrders() {
        if (mainContent != null) {
            mainContent.getChildren().clear();
            mainContent.getChildren().add(new Label("Orders Section Loaded"));
        }
    }

    @FXML
    private void openReports() {
        if (mainContent != null) {
            mainContent.getChildren().clear();
            mainContent.getChildren().add(new Label("Reports Section Loaded"));
        }
    }
}
