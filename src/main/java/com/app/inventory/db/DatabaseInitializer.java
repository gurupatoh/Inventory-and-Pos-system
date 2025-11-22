package com.app.inventory.db;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Creates tables if they don't exist and bootstraps a default admin.
 */
public class DatabaseInitializer {

    public static void initialize() {
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement()) {

            st.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL,
                    assigned_inventory_type TEXT NOT NULL,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                    last_login TEXT,
                    is_active INTEGER DEFAULT 1,
                    created_by TEXT
                );
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS inventory (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    quantity INTEGER NOT NULL,
                    price REAL NOT NULL,
                    type TEXT NOT NULL,
                    drink_category TEXT,
                    creator_id INTEGER NOT NULL,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (creator_id) REFERENCES users(id)
                );
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS session (
                    id INTEGER PRIMARY KEY,
                    username TEXT,
                    login_time TEXT
                );
            """);

            // Schema migrations for users table
            try {
                st.execute("ALTER TABLE users ADD COLUMN created_at TEXT DEFAULT CURRENT_TIMESTAMP");
                st.execute("ALTER TABLE users ADD COLUMN last_login TEXT");
                st.execute("ALTER TABLE users ADD COLUMN is_active INTEGER DEFAULT 1");
                st.execute("ALTER TABLE users ADD COLUMN created_by TEXT");
            } catch (SQLException e) {
                System.out.println("Schema migration ignored (columns may already exist): " + e.getMessage());
            }

            // Schema migrations for inventory table
            try {
                st.execute("ALTER TABLE inventory ADD COLUMN creator_id INTEGER");
                st.execute("ALTER TABLE inventory ADD COLUMN created_at TEXT DEFAULT CURRENT_TIMESTAMP");
                // Note: FOREIGN KEY can't be added via ALTER TABLE in SQLite; it's declaration only
            } catch (SQLException e) {
                System.out.println("Inventory schema migration ignored: " + e.getMessage());
            }

            st.execute("""
                CREATE TABLE IF NOT EXISTS audit_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER,
                    action TEXT NOT NULL,
                    details TEXT,
                    timestamp TEXT DEFAULT CURRENT_TIMESTAMP,
                    ip_address TEXT,
                    user_agent TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                );
            """);

            // bootstrap default admin always (replace if exists)
            String hashed = BCrypt.hashpw("admin123", BCrypt.gensalt(12));
            try (PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO users(username, password, role, assigned_inventory_type) VALUES (?, ?, ?, ?)")) {
                ps.setString(1, "admin");
                ps.setString(2, hashed);
                ps.setString(3, "ADMIN");
                ps.setString(4, "ALL");
                ps.executeUpdate();
            }

            System.out.println("Database initialized.");
        } catch (Exception e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
