package com.app.inventory.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.app.inventory.models.InventoryType;
import com.app.inventory.controllers.InventoryController;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SceneSwitcher {

    private static final Logger logger = Logger.getLogger(SceneSwitcher.class.getName());
    private static final String FXML_PATH = "/fxml/";

    public static void switchTo(Stage stage, String fxmlFile) {
        switchTo(stage, fxmlFile, null);
    }

    public static void switchTo(Stage stage, String fxmlFile, InventoryType type) {
        try {
            URL resource = SceneSwitcher.class.getResource(FXML_PATH + fxmlFile);
            if (resource == null) {
                logger.severe("FXML file not found: " + fxmlFile);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            // Pass pre-filter type if InventoryController
            Object controller = loader.getController();
            if (type != null && controller instanceof InventoryController inventoryController) {
                inventoryController.setFilterType(type);
            }

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load FXML: " + fxmlFile, e);
        }
    }
}
