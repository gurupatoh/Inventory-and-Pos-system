package com.app.inventory.controllers;

import com.app.inventory.auth.SessionManager;
import com.app.inventory.models.User;
import com.app.inventory.models.Role;
import com.app.inventory.models.InventoryType;
import com.app.inventory.services.AuditService;
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
    private Button inventoryButton;

    @FXML
    private VBox mainContent;

    @FXML
    private VBox adminMenu;

    @FXML
    private VBox staffMenu;

    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername());
        }

        setupMenuAccess();   // <--- Make sure menu visibility is applied
        loadDashboardContent();
    }

    private void setupMenuAccess() {
        if (currentUser.getRole() == Role.ADMIN) {
            if (adminMenu != null) adminMenu.setVisible(true);
            if (staffMenu != null) staffMenu.setVisible(true);
        } else if (currentUser.getRole() == Role.STAFF) {
            if (adminMenu != null) adminMenu.setVisible(false); // hide admin-only sections
            if (staffMenu != null) staffMenu.setVisible(true);   // show staff sections
        } else {
            if (adminMenu != null) adminMenu.setVisible(false);
            if (staffMenu != null) staffMenu.setVisible(false);
        }

        // Optional: disable inventory button if staff has no assigned inventory
        if (currentUser.getRole() == Role.STAFF && currentUser.getAssignedInventoryType() == null) {
            inventoryButton.setDisable(true);
        }
    }

    @FXML
    private void openInventory() {
        Stage stage = (Stage) inventoryButton.getScene().getWindow();

        if (currentUser.getRole() == Role.ADMIN) {
            SceneSwitcher.switchTo(stage, "inventory.fxml"); // Admin sees all
        } else if (currentUser.getRole() == Role.STAFF) {
            InventoryType type = currentUser.getAssignedInventoryType();
            if (type == InventoryType.CLUB) {
                SceneSwitcher.switchTo(stage, "club.fxml");
            } else if (type == InventoryType.RESTAURANT) {
                SceneSwitcher.switchTo(stage, "restaurant.fxml");
            } else {
                // Staff without assigned inventory can't access
                System.out.println("Access denied: no assigned inventory type");
                showAlert("Access Denied", "You are not assigned to any inventory type.", javafx.scene.control.Alert.AlertType.ERROR);
            }
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
    private void handleUserManagement() {
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        SceneSwitcher.switchTo(stage, "user_management.fxml");
    }

    @FXML
    private void openReports() {
        if (mainContent != null) {
            mainContent.getChildren().clear();
            mainContent.getChildren().add(new Label("Reports Section Loaded"));
        }
    }

    @FXML
    private void handleLogout() {
        // Log logout before clearing session
        User currentUser = SessionManager.getUser();
        if (currentUser != null) {
            AuditService.logLogout(currentUser, "127.0.0.1", "JavaFX App");
        }

        SessionManager.clearSession();
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        SceneSwitcher.switchTo(stage, "login.fxml");
    }

    private void loadDashboardContent() {
        if (mainContent != null) {
            mainContent.getChildren().clear();
            Label defaultLabel = new Label("Dashboard content goes here.");
            defaultLabel.setStyle("-fx-font-size: 16px;");
            mainContent.getChildren().add(defaultLabel);
        }
    }

    private void showAlert(String title, String msg, javafx.scene.control.Alert.AlertType type) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}
