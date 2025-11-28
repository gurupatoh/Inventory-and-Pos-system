package com.app.inventory.controllers;
import com.app.inventory.services.AuditService;
import com.app.inventory.auth.SessionManager;
import com.app.inventory.dao.InventoryDAO;
import com.app.inventory.models.InventoryItem;
import com.app.inventory.models.InventoryType;
import com.app.inventory.models.Role;
import com.app.inventory.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class AddEditInventoryController {
    private static final Logger logger = Logger.getLogger(AddEditInventoryController.class.getName());

    @FXML
    private TextField nameField;
    @FXML
    private Spinner<Integer> qtySpinner;
    @FXML
    private TextField priceField;
    @FXML
    private ComboBox<InventoryType> typeComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private InventoryItem editing;
    private User currentUser;

    @FXML
    public void initialize() {
        qtySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100000, 1));
        typeComboBox.getItems().addAll(InventoryType.CLUB, InventoryType.RESTAURANT);
        currentUser = SessionManager.getUser();

        logger.info("Initializing AddEditInventoryController for user: " +
                   (currentUser != null ? currentUser.getUsername() + " (" + currentUser.getRole() + ")" : "null"));

        // Restrict type for staff
        if (currentUser != null && currentUser.getRole() == Role.STAFF) {
            InventoryType assigned = currentUser.getAssignedInventoryType();
            logger.info("Staff user detected, restricting to assigned type: " + assigned);
            typeComboBox.setValue(assigned);
            typeComboBox.setDisable(true);
        } else {
            logger.info("Admin user or no user, allowing full type selection");
            typeComboBox.setValue(InventoryType.RESTAURANT); // default
        }
    }

    public void setInventoryItem(InventoryItem item) {
        this.editing = item;
        if (item != null) {
            nameField.setText(item.getName());
            qtySpinner.getValueFactory().setValue(item.getQuantity());
            priceField.setText(String.valueOf(item.getPrice()));
            typeComboBox.setValue(item.getType());
        }
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Name cannot be empty").showAndWait();
            return;
        }

        int qty = qtySpinner.getValue();
        if (qty < 0) {
            new Alert(Alert.AlertType.ERROR, "Quantity cannot be negative").showAndWait();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceField.getText().trim());
            if (price < 0) {
                new Alert(Alert.AlertType.ERROR, "Price cannot be negative").showAndWait();
                return;
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid price").showAndWait();
            return;
        }

        int creatorId = SessionManager.getCurrentUserId();
        if (creatorId <= 0) {
            new Alert(Alert.AlertType.ERROR, "No logged-in user found.").showAndWait();
            return;
        }

        InventoryType type = typeComboBox.getValue();
        logger.info("Processing save operation - selected type: " + type);
        if (currentUser != null && currentUser.getRole() == Role.STAFF) {
            InventoryType assigned = currentUser.getAssignedInventoryType();
            logger.info("Staff user detected, enforcing assigned type: " + assigned);
            type = assigned;
        }
        logger.info("Final inventory type for save: " + type);

        if (editing == null) {
            InventoryItem it = new InventoryItem(name, qty, price, type, creatorId);
            boolean success = InventoryDAO.insertInventory(it);
            if (success) {
                // Log audit for inventory add
                AuditService.logInventoryAdd(currentUser, name, "127.0.0.1", "JavaFX App");
                new Alert(Alert.AlertType.INFORMATION, "Item added").showAndWait();
                closeWindow();
            } else {
                new Alert(Alert.AlertType.ERROR, "Add failed").showAndWait();
            }
        } else {
            editing.setName(name);
            editing.setQuantity(qty);
            editing.setPrice(price);
            editing.setType(type);
            boolean ok = InventoryDAO.updateInventory(editing);
            if (ok) {
                // Log audit for inventory update
                AuditService.logInventoryUpdate(currentUser, name, "127.0.0.1", "JavaFX App");
                new Alert(Alert.AlertType.INFORMATION, "Item updated").showAndWait();
                closeWindow();
            } else {
                new Alert(Alert.AlertType.ERROR, "Update failed").showAndWait();
            }
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage s = (Stage) saveButton.getScene().getWindow();
        s.close();
    }
}
