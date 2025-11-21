package com.app.inventory.auth;

import com.app.inventory.models.User;
import com.app.inventory.services.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.app.inventory.utils.SceneSwitcher;



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

        User user = authService.login(username, password);

        if (user == null) {
            showAlert("Login Failed", "Invalid username or password!", Alert.AlertType.ERROR);
            return;
        }
        // store user in session
        SessionManager.setUser(user);

        // load dashboard
        Stage stage = (Stage) usernameField.getScene().getWindow();
        SceneSwitcher.switchTo(stage, "dashboard.fxml");
        // Success
        showAlert("Login Successful", "Welcome, " + user.getUsername() + " (" + user.getRole() + ")", Alert.AlertType.INFORMATION);

        // Next: open dashboard (Step 3 or 4)
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
