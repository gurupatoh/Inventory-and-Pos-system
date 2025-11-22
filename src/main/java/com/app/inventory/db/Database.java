package com.app.inventory.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private static final String URL = "jdbc:sqlite:inventory.db";

    static {
        try {
            // driver auto-loaded by driver jar, but explicit load is harmless:
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ignored) { }
    }

    private Database(){}

    public static Connection getConnection() throws SQLException {
        // Consider setting PRAGMA journal_mode=WAL etc. if needed.
        return DriverManager.getConnection(URL);
    }
}
