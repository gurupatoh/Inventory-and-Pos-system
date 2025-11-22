package com.app.inventory.dao;

import com.app.inventory.db.Database;
import com.app.inventory.models.InventoryItem;
import com.app.inventory.models.InventoryType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {

    public static List<InventoryItem> getAll() {
        List<InventoryItem> list = new ArrayList<>();
        String sql = "SELECT * FROM inventory ORDER BY created_at DESC";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                InventoryItem it = new InventoryItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        InventoryType.valueOf(rs.getString("type")),
                        rs.getString("drink_category"),
                        rs.getInt("creator_id")
                );
                it.setCreatedAt(rs.getString("created_at"));
                list.add(it);
            }
        } catch (Exception ex) {
            System.err.println("[InventoryDAO.getAll] " + ex.getMessage());
        }
        return list;
    }

    /**
     * Get inventory items filtered by type and creator (for RBAC)
     */
    public static List<InventoryItem> getByTypeAndCreator(InventoryType type, int creatorId) {
        List<InventoryItem> list = new ArrayList<>();
        String sql = "SELECT * FROM inventory WHERE type = ? AND creator_id = ? ORDER BY created_at DESC";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, type.name());
            ps.setInt(2, creatorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InventoryItem item = new InventoryItem(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            rs.getDouble("price"),
                            InventoryType.valueOf(rs.getString("type")),
                            rs.getString("drink_category"),
                            rs.getInt("creator_id")
                    );
                    item.setCreatedAt(rs.getString("created_at"));
                    list.add(item);
                }
            }
        } catch (Exception ex) {
            System.err.println("[InventoryDAO.getByTypeAndCreator] " + ex.getMessage());
        }
        return list;
    }

    /**
     * Get inventory items created by a specific user
     */
    public static List<InventoryItem> getByCreator(int creatorId) {
        List<InventoryItem> list = new ArrayList<>();
        String sql = "SELECT * FROM inventory WHERE creator_id = ? ORDER BY created_at DESC";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, creatorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InventoryItem item = new InventoryItem(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            rs.getDouble("price"),
                            InventoryType.valueOf(rs.getString("type")),
                            rs.getString("drink_category"),
                            rs.getInt("creator_id")
                    );
                    item.setCreatedAt(rs.getString("created_at"));
                    list.add(item);
                }
            }
        } catch (Exception ex) {
            System.err.println("[InventoryDAO.getByCreator] " + ex.getMessage());
        }
        return list;
    }

    public static List<InventoryItem> getByType(InventoryType type) {
        List<InventoryItem> list = new ArrayList<>();
        String sql = "SELECT * FROM inventory WHERE type = ? ORDER BY created_at DESC";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new InventoryItem(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            rs.getDouble("price"),
                            InventoryType.valueOf(rs.getString("type")),
                            rs.getString("drink_category"),
                            rs.getInt("creator_id")
                    ));
                }
            }
        } catch (Exception ex) {
            System.err.println("[InventoryDAO.getByType] " + ex.getMessage());
        }
        return list;
    }

    public static void add(InventoryItem item) {
        String sql = "INSERT INTO inventory(name, quantity, price, type, drink_category, creator_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, item.getName());
            ps.setInt(2, item.getQuantity());
            ps.setDouble(3, item.getPrice());
            ps.setString(4, item.getType().name());
            ps.setString(5, item.getDrinkCategory());
            ps.setInt(6, item.getCreatorId());
            ps.executeUpdate();
        } catch (Exception ex) {
            System.err.println("[InventoryDAO.add] " + ex.getMessage());
        }
    }

    public static void update(InventoryItem item) {
        String sql = "UPDATE inventory SET name=?, quantity=?, price=?, type=?, drink_category=? WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, item.getName());
            ps.setInt(2, item.getQuantity());
            ps.setDouble(3, item.getPrice());
            ps.setString(4, item.getType().name());
            ps.setString(5, item.getDrinkCategory());
            ps.setInt(6, item.getId());
            ps.executeUpdate();
        } catch (Exception ex) {
            System.err.println("[InventoryDAO.update] " + ex.getMessage());
        }
    }

    public static void deleteById(int id) {
        String sql = "DELETE FROM inventory WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception ex) {
            System.err.println("[InventoryDAO.deleteById] " + ex.getMessage());
        }
    }

    /**
     * Get the type of an item by its ID
     */
    public static InventoryType getItemType(int itemId) {
        String sql = "SELECT type FROM inventory WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return InventoryType.valueOf(rs.getString("type"));
                }
            }
        } catch (Exception ex) {
            System.err.println("[InventoryDAO.getItemType] " + ex.getMessage());
        }
        return null;
    }

    /**
     * Check if an item can be edited by the current user (based on type access)
     */
    public static boolean canEdit(int itemId, int currentUserId, InventoryType userAssignedType, boolean isAdmin) {
        if (isAdmin) return true;
        InventoryType itemType = getItemType(itemId);
        return itemType != null && itemType == userAssignedType;
    }

    /**
     * Check if an item can be deleted by the current user (same as edit permissions)
     */
    public static boolean canDelete(int itemId, int currentUserId, InventoryType userAssignedType, boolean isAdmin) {
        return canEdit(itemId, currentUserId, userAssignedType, isAdmin);
    }
}
