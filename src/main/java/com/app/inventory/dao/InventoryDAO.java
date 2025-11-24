package com.app.inventory.dao;

import com.app.inventory.db.Database;
import com.app.inventory.models.InventoryItem;
import com.app.inventory.models.InventoryType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class InventoryDAO {
    private static final Logger logger = Logger.getLogger(InventoryDAO.class.getName());

    public static boolean insertInventory(InventoryItem item) {
        String sql = "INSERT INTO inventory(name, quantity, price, type, creator_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setInt(2, item.getQuantity());
            ps.setDouble(3, item.getPrice());
            ps.setString(4, item.getType().name());
            ps.setInt(5, item.getCreatorId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            logger.severe("[InventoryDAO.insertInventory] " + ex.getMessage());
            return false;
        }
    }

    public static List<InventoryItem> getInventoryByType(InventoryType type) {
        List<InventoryItem> list = new ArrayList<>();
        String sql = "SELECT * FROM inventory WHERE type = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InventoryItem it = new InventoryItem(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            rs.getDouble("price"),
                            InventoryType.valueOf(rs.getString("type")),
                            rs.getInt("creator_id"),
                            rs.getString("created_at")
                    );
                    list.add(it);
                }
            }
        } catch (SQLException ex) {
            logger.severe("[InventoryDAO.getInventoryByType] " + ex.getMessage());
        }
        return list;
    }

    public static boolean deleteInventory(int id) {
        String sql = "DELETE FROM inventory WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            logger.severe("[InventoryDAO.deleteInventory] " + ex.getMessage());
            return false;
        }
    }

    public static boolean updateInventory(InventoryItem item) {
        String sql = "UPDATE inventory SET name=?, quantity=?, price=?, type=? WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setInt(2, item.getQuantity());
            ps.setDouble(3, item.getPrice());
            ps.setString(4, item.getType().name());
            ps.setInt(5, item.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            logger.severe("[InventoryDAO.updateInventory] " + ex.getMessage());
            return false;
        }
    }
}

