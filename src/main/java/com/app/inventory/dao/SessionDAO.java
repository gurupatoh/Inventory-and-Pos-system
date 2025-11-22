package com.app.inventory.dao;

import com.app.inventory.db.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class SessionDAO {

    public static void saveSession(String username) {
        String sql = "REPLACE INTO session(id, username, login_time) VALUES (1, ?, datetime('now'))";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (Exception ex) {
            System.err.println("[SessionDAO.saveSession] " + ex.getMessage());
        }
    }

    public static String getLastUsername() {
        String sql = "SELECT username FROM session WHERE id = 1";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString("username");
        } catch (Exception ex) {
            System.err.println("[SessionDAO.getLastUsername] " + ex.getMessage());
        }
        return null;
    }

    public static void clear() {
        try (Connection c = Database.getConnection();
             Statement st = c.createStatement()) {
            st.execute("DELETE FROM session");
        } catch (Exception ex) {
            System.err.println("[SessionDAO.clear] " + ex.getMessage());
        }
    }

}