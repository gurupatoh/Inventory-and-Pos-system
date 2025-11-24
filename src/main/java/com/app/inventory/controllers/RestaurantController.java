package com.app.inventory.controllers;

import com.app.inventory.models.InventoryType;
import com.app.inventory.models.InventoryItem;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.List;

/**
 * Controller for Restaurant Inventory management.
 * Forces the inventory view to show only RESTAURANT items.
 */
public class RestaurantController extends InventoryController {

    // FXML fields that need to be accessible by the FXML loader
    @FXML
    private TableView<InventoryItem> inventoryTable;
    @FXML
    private TableColumn<InventoryItem, String> colName;
    @FXML
    private TableColumn<InventoryItem, Integer> colQuantity;
    @FXML
    private TableColumn<InventoryItem, Double> colPrice;
    @FXML
    private TableColumn<InventoryItem, String> colType;
    @FXML
    private TableColumn<InventoryItem, String> colTotal;
    @FXML
    private ComboBox<String> filterTypeCombo;
    @FXML
    private Label totalValueLabel;

    @Override
    public void initialize() {
        setFilterType(InventoryType.RESTAURANT);
        super.initialize();
    }

    // Override the FXML handler methods to ensure they're available
    @FXML
    public void handleDelete(MouseEvent event) {
        super.handleDelete(event);
    }

    @FXML
    public void handleAddItem() {
        super.openAddEditDialog(null);
    }

    @FXML
    public void handleEditItem() {
        InventoryItem selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            // Show alert or do nothing
            return;
        }
        super.openAddEditDialog(selected);
    }

    @FXML
    public void handleDeleteItem() {
        super.handleDelete(null);
    }

    @FXML
    public void handleBack() {
        super.handleBack();
    }
}
