package com.app.inventory.controllers;

import com.app.inventory.auth.SessionManager;
import com.app.inventory.models.User;
import com.app.inventory.models.Role;
import com.app.inventory.models.InventoryType;
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
            welcomeLabel.setText("Welcome, " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
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
            SceneSwitcher.switchTo(stage, "inventory.fxml", null); // Admin sees all
        } else if (currentUser.getRole() == Role.STAFF) {
            InventoryType type = currentUser.getAssignedInventoryType();
            if (type != null) {
                SceneSwitcher.switchTo(stage, "inventory.fxml", type); // Staff sees only assigned type
            } else {
                // Staff without assigned inventory can't access
                System.out.println("Access denied: no assigned inventory type");
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
    private void openReports() {
        if (mainContent != null) {
            mainContent.getChildren().clear();
            mainContent.getChildren().add(new Label("Reports Section Loaded"));
        }
    }

    @FXML
    private void handleLogout() {
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
}
