package com.app.inventory.db;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.logging.Logger;

public class Database {
    private static final Logger logger = Logger.getLogger(Database.class.getName());
    private static final String DB_FILE = "inventory.db";
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_FILE;

    static {
        init();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }

    private static void init() {
        try {
            // ensure file exists
            if (!Files.exists(Paths.get(DB_FILE))) {
                Files.createFile(Paths.get(DB_FILE));
            }
        } catch (Exception e) {
            logger.severe("Unable to create DB file: " + e.getMessage());
        }

        // run migrations / ensure tables exist
        try (Connection c = getConnection();
             Statement s = c.createStatement()) {

            // users table (simple schema; adapt columns as needed)
            s.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL,
                    assigned_inventory_type TEXT NOT NULL DEFAULT 'ALL',
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                    last_login TEXT,
                    is_active INTEGER DEFAULT 1
                );
            """);

            // inventory table with creator_id and created_at
            s.execute("""
                CREATE TABLE IF NOT EXISTS inventory (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    quantity INTEGER NOT NULL,
                    price REAL NOT NULL,
                    type TEXT NOT NULL,
                    creator_id INTEGER NOT NULL,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (creator_id) REFERENCES users(id)
                );
            """);

            // audit logs (simple)
            s.execute("""
                CREATE TABLE IF NOT EXISTS audit_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER,
                    action TEXT,
                    details TEXT,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP
                );
            """);

            logger.info("Database initialized.");
        } catch (SQLException ex) {
            logger.severe("Database init error: " + ex.getMessage());
        }
    }
}
