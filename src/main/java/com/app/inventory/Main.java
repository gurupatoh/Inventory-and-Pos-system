package com.app.inventory;

import com.app.inventory.db.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Initialize database tables and bootstrap admin
        DatabaseInitializer.initialize();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/login.fxml")
        );

        Scene scene = new Scene(loader.load());

        // Load CSS stylesheet
        String css = getClass().getResource("/css/main.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Login - Inventory System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}