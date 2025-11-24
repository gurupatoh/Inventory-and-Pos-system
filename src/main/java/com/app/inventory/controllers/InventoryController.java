package com.app.inventory.controllers;

import com.app.inventory.dao.InventoryDAO;
import com.app.inventory.models.InventoryItem;
import com.app.inventory.models.InventoryType;
import com.app.inventory.auth.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.app.inventory.utils.SceneSwitcher;
import java.net.URL;
import java.util.List;

public class InventoryController {

    @FXML
    private TableView<InventoryItem> inventoryTable;
    @FXML
    private TableColumn<InventoryItem, String> colName;
    @FXML
    private TableColumn<InventoryItem, String> colType;
    @FXML
    private TableColumn<InventoryItem, Integer> colQuantity;
    @FXML
    private TableColumn<InventoryItem, Double> colPrice;
    @FXML
    private TableColumn<InventoryItem, String> colTotal;
    @FXML
    private TableColumn<InventoryItem, String> colDate;
    @FXML
    private ComboBox<String> filterTypeCombo;
    @FXML
    private Label totalValueLabel;

    private InventoryType filterType;

    public void initialize() {
        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        colQuantity.setCellValueFactory(new PropertyValueFactory<InventoryItem, Integer>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<InventoryItem, Double>("price"));
        colType.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType().name()));
        colTotal.setCellValueFactory(cell -> new SimpleStringProperty(
                String.format("$%.2f", cell.getValue().getQuantity() * cell.getValue().getPrice())));
        colDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCreatedAt()));

        // Setup filter combo box
        if (SessionManager.getUser() != null && SessionManager.getUser().getRole().name().equals("STAFF")) {
            // Staff can only see their assigned type (no ALL option)
            InventoryType assigned = SessionManager.getUser().getAssignedInventoryType();
            filterTypeCombo.setItems(FXCollections.observableArrayList(assigned.name()));
            filterType = assigned; // Default to their type
            filterTypeCombo.setValue(assigned.name());
        } else {
            // Admin sees all types including ALL
            filterTypeCombo.setItems(FXCollections.observableArrayList(
                    InventoryType.CLUB.name(), InventoryType.RESTAURANT.name(), InventoryType.ALL.name()
            ));
            filterType = InventoryType.ALL; // Default to all
            filterTypeCombo.setValue(InventoryType.ALL.name());
        }

        filterTypeCombo.setOnAction(e -> {
            String selected = filterTypeCombo.getValue();
            filterType = InventoryType.valueOf(selected);
            loadData();
        });

        loadData();
    }

    public void setFilterType(InventoryType type) {
        this.filterType = type;
        filterTypeCombo.setValue(type.name());
        loadData();
    }

    public void loadData() {
        if (filterType == null) filterType = InventoryType.ALL;
        List<InventoryItem> items;
        if (filterType == InventoryType.ALL) {
            items = InventoryDAO.getInventoryByType(InventoryType.CLUB);
            items.addAll(InventoryDAO.getInventoryByType(InventoryType.RESTAURANT));
        } else {
            items = InventoryDAO.getInventoryByType(filterType);
        }
        inventoryTable.setItems(FXCollections.observableArrayList(items));

        // Calculate total inventory value
        double totalValue = items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();

        if (totalValueLabel != null) {
            totalValueLabel.setText(String.format("Total Inventory Value: $%.2f", totalValue));
        }
    }

    @FXML
    public void handleDelete(MouseEvent event) {
        InventoryItem selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (InventoryDAO.deleteInventory(selected.getId())) {
                loadData();
            }
        }
    }

    @FXML
    public void handleAddItem() {
        openAddEditDialog(null);
    }

    @FXML
    public void handleEditItem() {
        InventoryItem selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            // Show alert or do nothing
            return;
        }
        openAddEditDialog(selected);
    }

    void openAddEditDialog(InventoryItem item) {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL res = getClass().getResource("/fxml/add_edit_inventory.fxml");
            if (res == null) {
                System.err.println("FXML not found: /fxml/add_edit_inventory.fxml");
                return;
            }
            loader.setLocation(res);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(item == null ? "Add Inventory Item" : "Edit Inventory Item");
            AddEditInventoryController controller = loader.getController();
            if (item != null) {
                controller.setInventoryItem(item);
            }
            stage.showAndWait(); // wait for dialog to close
            loadData(); // refresh table
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteItem() {
        // Alias for handleDelete, or implement separate logic if needed
        handleDelete(null);
    }

    @FXML
    public void handleBack() {
        Stage currentStage = (Stage) inventoryTable.getScene().getWindow();
        SceneSwitcher.switchTo(currentStage, "dashboard.fxml");
    }
}
