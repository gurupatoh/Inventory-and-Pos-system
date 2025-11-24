package com.app.inventory.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

public class SceneSwitcher {
    private static final Logger logger = Logger.getLogger(SceneSwitcher.class.getName());
    private static final String PREFIX = "/fxml/";

    public static void switchTo(Stage stage, String fxmlFile) {
        try {
            String fullPath = PREFIX + fxmlFile;
            logger.info("Attempting to load FXML: " + fullPath);
            URL res = SceneSwitcher.class.getResource(fullPath);
            if (res == null) {
                logger.severe("FXML resource not found at classpath: " + fullPath);
                logger.severe("Current classpath resource: " + res);
                return;
            }
            logger.info("FXML resource found at: " + res.toString());
            Parent root = FXMLLoader.load(res);
            stage.setScene(new Scene(root));
            stage.show();
            logger.info("Successfully loaded FXML: " + fxmlFile);
        } catch (Exception ex) {
            logger.severe("Failed to load FXML: " + fxmlFile + " - " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
