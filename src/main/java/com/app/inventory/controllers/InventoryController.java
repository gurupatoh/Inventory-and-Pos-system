package com.app.inventory.controllers;

import com.app.inventory.auth.SessionManager;
import com.app.inventory.dao.InventoryDAO;
import com.app.inventory.models.User;
import com.app.inventory.models.InventoryItem;
import com.app.inventory.models.InventoryType;
import com.app.inventory.models.Role;
import com.app.inventory.services.AuditService;
import com.app.inventory.utils.SceneSwitcher;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class InventoryController {

    @FXML
    private TableView<InventoryItem> inventoryTable;

    @FXML
    private TableColumn<InventoryItem, String> colName;

    @FXML
    private TableColumn<InventoryItem, Integer> colQuantity;

    @FXML
    private TableColumn<InventoryItem, Double> colPrice;

    @FXML
    private TableColumn<InventoryItem, InventoryType> colType;

    @FXML
    private TableColumn<InventoryItem, Double> colTotal;

    @FXML
    private ComboBox<String> filterTypeCombo;

    @FXML
    private Label totalValueLabel;

    private ObservableList<InventoryItem> inventoryList;
    private InventoryType forcedType;

    @FXML
    public void initialize() {
        setupTable();
        setupFilters();
        loadInventoryData();

        filterTypeCombo.setOnAction(e -> applyFilters());
    }

    private void setupTable() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colTotal.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().getTotalPrice()).asObject());
    }

    private void setupFilters() {
        User user = SessionManager.getUser();
        com.app.inventory.utils.AccessControl accessControl = new com.app.inventory.utils.AccessControl(user);
        if (accessControl.isAdmin()) {
            filterTypeCombo.getItems().addAll("All", "CLUB", "RESTAURANT");
            filterTypeCombo.setValue("All");
        } else {
            // Staff can filter by both types, but can only access/view their assigned type
            filterTypeCombo.getItems().addAll("CLUB", "RESTAURANT");
            filterTypeCombo.setValue(user.getAssignedInventoryType().name());
        }
    }

    private void loadInventoryData() {
        User user = SessionManager.getUser();
        com.app.inventory.utils.AccessControl accessControl = new com.app.inventory.utils.AccessControl(user);

        String selectedFilter = filterTypeCombo.getValue();
        InventoryType filterType = null;
        if (selectedFilter != null && !selectedFilter.equals("All")) {
            filterType = InventoryType.valueOf(selectedFilter);
        }

        if (accessControl.isAdmin()) {
            if (filterType == null || filterType == InventoryType.ALL) {
                inventoryList = FXCollections.observableArrayList(InventoryDAO.getAll());
            } else {
                inventoryList = FXCollections.observableArrayList(InventoryDAO.getByType(filterType));
            }
        } else {
            // Staff can only see items of their assigned type
            if (filterType != null && accessControl.canAccessInventory(filterType)) {
                inventoryList = FXCollections.observableArrayList(InventoryDAO.getByType(filterType));
            } else {
                inventoryList = FXCollections.observableArrayList();
            }
        }
        inventoryTable.setItems(inventoryList);
        updateTotalLabel();

        if (user != null) {
            AuditService.logInventoryAccess(user, selectedFilter != null ? selectedFilter : "ALL", "127.0.0.1", "JavaFX App");
        }
    }

    private void updateTotalLabel() {
        double total = inventoryTable.getItems().stream().mapToDouble(InventoryItem::getTotalPrice).sum();
        totalValueLabel.setText("Total Inventory Value: $" + String.format("%.2f", total));
    }

    @FXML
    private void applyFilters() {
        String selectedType = filterTypeCombo.getValue();
        if (selectedType == null || selectedType.equals("All")) {
            inventoryTable.setItems(inventoryList);
        } else {
            InventoryType type = InventoryType.valueOf(selectedType);
            ObservableList<InventoryItem> filteredList = FXCollections.observableArrayList(
                    inventoryList.filtered(item -> item.getType() == type)
            );
            inventoryTable.setItems(filteredList);
        }
        updateTotalLabel();
    }

    public void setFilterType(InventoryType type) {
        this.forcedType = type;
    }

    @FXML
    private void handleAddItem() {
        addOrEditItem(null);
    }

    @FXML
    private void handleEditItem() {
        InventoryItem selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an item to edit.", Alert.AlertType.WARNING);
            return;
        }
        User currentUser = SessionManager.getUser();
        com.app.inventory.utils.AccessControl accessControl = new com.app.inventory.utils.AccessControl(currentUser);
        if (!accessControl.canModifyInventoryItem(selected.getType())) {
            showAlert("Access Denied", "You can only edit items of your assigned type.", Alert.AlertType.ERROR);
            return;
        }
        addOrEditItem(selected);
    }

    @FXML
    private void handleDeleteItem() {
        InventoryItem selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an item to delete.", Alert.AlertType.WARNING);
            return;
        }
        User currentUser = SessionManager.getUser();
        com.app.inventory.utils.AccessControl accessControl = new com.app.inventory.utils.AccessControl(currentUser);
        if (!accessControl.canModifyInventoryItem(selected.getType())) {
            showAlert("Access Denied", "You can only delete items of your assigned type.", Alert.AlertType.ERROR);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Item");
        confirm.setHeaderText("Are you sure you want to delete '" + selected.getName() + "'?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                InventoryDAO.deleteById(selected.getId());
                User currentUser2 = SessionManager.getUser();
                if (currentUser2 != null) {
                    AuditService.logInventoryDelete(currentUser2, selected.getName(), "127.0.0.1", "JavaFX App");
                }
                loadInventoryData();
                showAlert("Success", "Item deleted successfully.", Alert.AlertType.INFORMATION);
            }
        });
    }

    private void addOrEditItem(InventoryItem existing) {
        boolean isEdit = existing != null;

        TextInputDialog nameDialog = new TextInputDialog(isEdit ? existing.getName() : "");
        nameDialog.setTitle(isEdit ? "Edit Item" : "Add Item");
        nameDialog.setHeaderText("Enter Item Name");
        nameDialog.setContentText("Name:");

        nameDialog.showAndWait().ifPresent(name -> {
            if (name.trim().isEmpty()) {
                showAlert("Invalid Input", "Item name cannot be empty.", Alert.AlertType.ERROR);
                return;
            }

            TextInputDialog qtyDialog = new TextInputDialog(isEdit ? String.valueOf(existing.getQuantity()) : "");
            qtyDialog.setTitle(isEdit ? "Edit Item" : "Add Item");
            qtyDialog.setHeaderText("Enter Quantity");
            qtyDialog.setContentText("Quantity:");

            qtyDialog.showAndWait().ifPresent(qtyStr -> {
                try {
                    int qty = Integer.parseInt(qtyStr.trim());
                    if (qty <= 0) throw new NumberFormatException();

                    TextInputDialog priceDialog = new TextInputDialog(isEdit ? String.valueOf(existing.getPrice()) : "");
                    priceDialog.setTitle(isEdit ? "Edit Item" : "Add Item");
                    priceDialog.setHeaderText("Enter Price");
                    priceDialog.setContentText("Price:");

                    priceDialog.showAndWait().ifPresent(priceStr -> {
                        try {
                            double price = Double.parseDouble(priceStr.trim());
                            if (price <= 0) throw new NumberFormatException();

                            User currentUser = SessionManager.getUser();
                            com.app.inventory.utils.AccessControl accessControl = new com.app.inventory.utils.AccessControl(currentUser);

                            if (accessControl.isAdmin()) {
                                // Admin can choose type
                                InventoryType defaultType = isEdit ? existing.getType() : InventoryType.RESTAURANT;

                                ChoiceDialog<InventoryType> typeDialog = new ChoiceDialog<>(defaultType,
                                        InventoryType.CLUB, InventoryType.RESTAURANT);
                                typeDialog.setTitle(isEdit ? "Edit Item" : "Add Item");
                                typeDialog.setHeaderText("Select Inventory Type");
                                typeDialog.setContentText("Type:");

                                typeDialog.showAndWait().ifPresent(selectedType -> {
                                    // FIX: Ensure drinkCategory is not null to prevent database issues
                                    String defaultDrinkCategory = "Beer"; // Reasonable default

                                    InventoryItem item = new InventoryItem(0, name.trim(), qty, price, selectedType, defaultDrinkCategory);
                                    performAddOrEdit(isEdit, existing, item, currentUser);
                                });
                            } else {
                                // Staff can only create/edit their assigned type
                                InventoryType assignedType = currentUser.getAssignedInventoryType();
                                InventoryType itemType = isEdit ? existing.getType() : (forcedType != null ? forcedType : assignedType);

                                if (isEdit && itemType != assignedType) {
                                    showAlert("Access Denied", "You cannot edit items of different type than your assignment.", Alert.AlertType.ERROR);
                                    return;
                                }

                                // FIX: Ensure drinkCategory is not null to prevent database issues
                                String defaultDrinkCategory = "Beer"; // Reasonable default

                                InventoryItem item = new InventoryItem(0, name.trim(), qty, price, itemType, defaultDrinkCategory);
                                performAddOrEdit(isEdit, existing, item, currentUser);
                            }
                        } catch (NumberFormatException e) {
                            showAlert("Invalid Input", "Price must be a positive number.", Alert.AlertType.ERROR);
                        }
                    });
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Quantity must be a positive integer.", Alert.AlertType.ERROR);
                }
            });
        });
    }

    private void performAddOrEdit(boolean isEdit, InventoryItem existing, InventoryItem item, User currentUser) {
        if (isEdit) {
            item.setId(existing.getId());
            InventoryDAO.update(item);
            AuditService.logInventoryUpdate(currentUser, item.getName(), "127.0.0.1", "JavaFX App");
            showAlert("Success", "Item updated successfully.", Alert.AlertType.INFORMATION);
        } else {
            // Set the creator ID for new items
            item.setCreatorId(currentUser.getId());
            InventoryDAO.add(item);
            AuditService.logInventoryAdd(currentUser, item.getName(), "127.0.0.1", "JavaFX App");
            showAlert("Success", "Item added successfully.", Alert.AlertType.INFORMATION);
        }
        loadInventoryData();
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {

        Stage stage = (Stage) inventoryTable.getScene().getWindow();
        SceneSwitcher.switchTo(stage, "dashboard.fxml");
    }
}
