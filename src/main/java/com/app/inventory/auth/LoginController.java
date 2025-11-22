package com.app.inventory.auth;

import com.app.inventory.models.InventoryType;
import com.app.inventory.models.Role;
import com.app.inventory.models.User;
import com.app.inventory.services.AuthService;
import com.app.inventory.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Authenticate via AuthService
        User user = authService.login(username, password, "127.0.0.1", "JavaFX App");

        if (user == null) {
            showAlert("Login Failed", "Invalid username or password!", Alert.AlertType.ERROR);
            return;
        }

        // Assign inventory type for staff users
        if (user.getRole() == Role.STAFF) {
            if (username.toLowerCase().contains("restaurant")) {
                user.setAssignedInventoryType(InventoryType.RESTAURANT);
            } else if (username.toLowerCase().contains("club")) {
                user.setAssignedInventoryType(InventoryType.CLUB);
            } else {
                // default staff inventory type if none specified
                user.setAssignedInventoryType(InventoryType.RESTAURANT);
            }
        } else {
            // Admin sees all inventory
            user.setAssignedInventoryType(null);
        }

        // Store user in session
        SessionManager.setUser(user);

        // Navigate to dashboard
        Stage stage = (Stage) usernameField.getScene().getWindow();
        SceneSwitcher.switchTo(stage, "dashboard.fxml");

        // Optional: show login success message
        showAlert("Login Successful", "Welcome, " + user.getUsername() + " (" + user.getRole() + ")", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
