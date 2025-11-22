package com.app.inventory.dao;

import com.app.inventory.db.Database;
import com.app.inventory.models.AuditLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditDAO {

    public static void logAudit(Integer userId, String action, String details, String ipAddress, String userAgent) {
        String sql = "INSERT INTO audit_logs(user_id, action, details, ip_address, user_agent) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (userId != null) {
                ps.setInt(1, userId);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, action);
            ps.setString(3, details);
            ps.setString(4, ipAddress);
            ps.setString(5, userAgent);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("[AuditDAO.logAudit] " + ex.getMessage());
        }
    }

    public static List<AuditLog> getAllAuditLogs() {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs ORDER BY timestamp DESC";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AuditLog log = new AuditLog(
                        rs.getInt("id"),
                        rs.getObject("user_id") != null ? rs.getInt("user_id") : null,
                        rs.getString("action"),
                        rs.getString("details"),
                        rs.getString("timestamp"),
                        rs.getString("ip_address"),
                        rs.getString("user_agent")
                );
                list.add(log);
            }
        } catch (Exception ex) {
            System.err.println("[AuditDAO.getAllAuditLogs] " + ex.getMessage());
        }
        return list;
    }

    public static List<AuditLog> getAuditLogsByUser(int userId) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs WHERE user_id = ? ORDER BY timestamp DESC";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuditLog log = new AuditLog(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("action"),
                            rs.getString("details"),
                            rs.getString("timestamp"),
                            rs.getString("ip_address"),
                            rs.getString("user_agent")
                    );
                    list.add(log);
                }
            }
        } catch (Exception ex) {
            System.err.println("[AuditDAO.getAuditLogsByUser] " + ex.getMessage());
        }
        return list;
    }

    public static List<AuditLog> getAuditLogsByAction(String action) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs WHERE action = ? ORDER BY timestamp DESC";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, action);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuditLog log = new AuditLog(
                            rs.getInt("id"),
                            rs.getObject("user_id") != null ? rs.getInt("user_id") : null,
                            rs.getString("action"),
                            rs.getString("details"),
                            rs.getString("timestamp"),
                            rs.getString("ip_address"),
                            rs.getString("user_agent")
                    );
                    list.add(log);
                }
            }
        } catch (Exception ex) {
            System.err.println("[AuditDAO.getAuditLogsByAction] " + ex.getMessage());
        }
        return list;
    }
}
