package com.app.inventory.controllers;

import com.app.inventory.models.User;
import com.app.inventory.services.UserManagementService;
import com.app.inventory.models.Role;
import com.app.inventory.models.InventoryType;
import com.app.inventory.services.AuditService;
import com.app.inventory.models.AuditLog;
import com.app.inventory.utils.SceneSwitcher;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class UserManagementController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colInventoryType;
    @FXML private TableColumn<User, String> colLastLogin;
    @FXML private TableColumn<User, Boolean> colActive;

    @FXML private TextField filterUsername;

    private User currentAdmin;
    private ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        currentAdmin = com.app.inventory.auth.SessionManager.getUser();
        if (currentAdmin == null || currentAdmin.getRole() != Role.ADMIN) {
            showAlert("Access Denied", "Only admins can access user management.", Alert.AlertType.ERROR);
            return;
        }

        setupTable();
        loadUsers();
        filterUsername.textProperty().addListener((obs, oldText, newText) -> filterUsers());
    }

    private void setupTable() {
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colInventoryType.setCellValueFactory(cellData -> {
            InventoryType type = cellData.getValue().getAssignedInventoryType();
            return new javafx.beans.property.SimpleStringProperty(type != null ? type.name() : "ALL");
        });
        colLastLogin.setCellValueFactory(new PropertyValueFactory<>("lastLogin"));
        colActive.setCellValueFactory(new PropertyValueFactory<>("active"));
    }

    private void loadUsers() {
        List<User> users = UserManagementService.getAllUsers(currentAdmin);
        if (users != null) {
            userList.setAll(users);
            userTable.setItems(userList);
        }
    }

    @FXML
    private void handleAddUser() {
        // Simple dialog for demo
        TextInputDialog usernameDialog = new TextInputDialog();
        usernameDialog.setTitle("Add User");
        usernameDialog.setHeaderText("Enter Username");
        usernameDialog.setContentText("Username:");

        usernameDialog.showAndWait().ifPresent(username -> {
            TextInputDialog passwordDialog = new TextInputDialog();
            passwordDialog.setTitle("Add User");
            passwordDialog.setHeaderText("Enter Password");
            passwordDialog.setContentText("Password:");

            passwordDialog.showAndWait().ifPresent(password -> {
                ChoiceDialog<String> roleDialog = new ChoiceDialog<>("STAFF", "ADMIN", "STAFF");
                roleDialog.setTitle("Add User");
                roleDialog.setHeaderText("Select Role");
                roleDialog.setContentText("Role:");

                roleDialog.showAndWait().ifPresent(role -> {
                    ChoiceDialog<String> typeDialog = new ChoiceDialog<>("RESTAURANT", "CLUB", "RESTAURANT", "ALL");
                    typeDialog.setTitle("Add User");
                    typeDialog.setHeaderText("Select Inventory Type");
                    typeDialog.setContentText("Type:");

                    typeDialog.showAndWait().ifPresent(type -> {
                        User newUser = new User(username, password, Role.valueOf(role),
                                type.equals("ALL") ? InventoryType.ALL : InventoryType.valueOf(type));
                        boolean success = UserManagementService.addUser(currentAdmin, newUser, "127.0.0.1", "JavaFX App");
                        if (success) {
                            showAlert("Success", "User added successfully.", Alert.AlertType.INFORMATION);
                            loadUsers();
                        } else {
                            showAlert("Error", "Failed to add user.", Alert.AlertType.ERROR);
                        }
                    });
                });
            });
        });
    }

    @FXML
    private void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a user to delete.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete User");
        confirm.setHeaderText("Are you sure you want to delete " + selected.getUsername() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = UserManagementService.deleteUser(currentAdmin, selected.getId(), "127.0.0.1", "JavaFX App");
                if (success) {
                    showAlert("Success", "User deleted successfully.", Alert.AlertType.INFORMATION);
                    loadUsers();
                } else {
                    showAlert("Error", "Failed to delete user.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    private void handleResetPassword() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a user to reset password.", Alert.AlertType.WARNING);
            return;
        }

        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Reset Password");
        passwordDialog.setHeaderText("Enter New Password");
        passwordDialog.setContentText("New Password:");

        passwordDialog.showAndWait().ifPresent(newPassword -> {
            boolean success = UserManagementService.adminResetPassword(currentAdmin, selected.getId(), newPassword, "127.0.0.1", "JavaFX App");
            if (success) {
                showAlert("Success", "Password reset successfully.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to reset password.", Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleViewAuditLogs() {
        List<AuditLog> logs = AuditService.getAllAuditLogs(currentAdmin);
        if (logs != null) {
            StringBuilder sb = new StringBuilder();
            for (AuditLog log : logs) {
                sb.append(log.getTimestamp()).append(" - ").append(log.getAction()).append(" by ").append(log.getUserId()).append(": ").append(log.getDetails()).append("\n");
            }
            TextArea textArea = new TextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Audit Logs");
            alert.setHeaderText("Recent Audit Logs");
            alert.getDialogPane().setContent(textArea);
            alert.showAndWait();
        }
    }

    private void filterUsers() {
        String filter = filterUsername.getText().toLowerCase();
        if (filter.isEmpty()) {
            userTable.setItems(userList);
        } else {
            ObservableList<User> filtered = userList.filtered(user -> user.getUsername().toLowerCase().contains(filter));
            userTable.setItems(filtered);
        }
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) userTable.getScene().getWindow();
        SceneSwitcher.switchTo(stage, "dashboard.fxml");
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
