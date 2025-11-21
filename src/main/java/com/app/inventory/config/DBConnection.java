package com.app.inventory.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(AppConfig.DB_URL);
                System.out.println("Database connected successfully.");
            } catch (SQLException e) {
                throw new RuntimeException("Database connection failed: " + e.getMessage());
            }
        }
        return connection;
    }
}
