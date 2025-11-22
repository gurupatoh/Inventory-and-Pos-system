package com.app.inventory.controllers;

import com.app.inventory.auth.SessionManager;
import com.app.inventory.dao.InventoryDAO;
import com.app.inventory.models.User;
import com.app.inventory.models.InventoryItem;
import com.app.inventory.models.InventoryType;
import com.app.inventory.services.AuditService;
import com.app.inventory.utils.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    private ComboBox<String> filterTypeCombo;

    private ObservableList<InventoryItem> inventoryList;
    private InventoryType forcedType; // set by SceneSwitcher

    @FXML
    public void initialize() {
        setupTable();
        loadFilterOptions();
        loadInventoryData();

        // Apply pre-filter if staff
        if (forcedType != null) {
            filterTypeCombo.setValue(forcedType.toString());
            applyFilters();
        }

        filterTypeCombo.setOnAction(e -> applyFilters());
    }

    /**
     * Map table columns to InventoryItem fields
     */
    private void setupTable() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
    }

    /**
     * Load ComboBox filter options
     */
    private void loadFilterOptions() {
        filterTypeCombo.getItems().addAll("All", "CLUB", "RESTAURANT");
        filterTypeCombo.setValue("All");  // default
    }

    /**
     * Load all inventory items into the table
     */
    private void loadInventoryData() {
        if (forcedType == null || forcedType == InventoryType.ALL) {
            inventoryList = FXCollections.observableArrayList(InventoryDAO.getAll());
        } else {
            inventoryList = FXCollections.observableArrayList(InventoryDAO.getByType(forcedType));
        }
        inventoryTable.setItems(inventoryList);

        // Log audit
        User user = SessionManager.getUser();
        if (user != null) {
            AuditService.logInventoryAccess(user, forcedType != null ? forcedType.name() : "ALL", "127.0.0.1", "JavaFX App");
        }
    }

    /**
     * Apply filter based on selected type
     */
    private void applyFilters() {
        String selectedType = filterTypeCombo.getValue();
        if (selectedType == null || selectedType.equals("All")) {
            inventoryTable.setItems(inventoryList);
            return;
        }

        InventoryType type = InventoryType.valueOf(selectedType);

        ObservableList<InventoryItem> filteredList = FXCollections.observableArrayList(
                inventoryList.filtered(item -> item.getType() == type)
        );

        inventoryTable.setItems(filteredList);
    }

    public void setFilterType(InventoryType type) {
        this.forcedType = type;
    }

    @FXML
    private void handleAddItem() {
        System.out.println("Add Item clicked");
    }

    @FXML
    private void handleEditItem() {
        System.out.println("Edit Item clicked");
    }

    @FXML
    private void handleDeleteItem() {
        System.out.println("Delete Item clicked");
    }

    @FXML
    private void handleBack() {
        // Example: Go back to dashboard
        Stage stage = (Stage) inventoryTable.getScene().getWindow();
        SceneSwitcher.switchTo(stage, "dashboard.fxml");
    }
}
