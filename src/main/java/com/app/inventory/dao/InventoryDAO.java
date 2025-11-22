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
        String sql = "SELECT * FROM inventory";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                InventoryItem it = new InventoryItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        InventoryType.valueOf(rs.getString("type"))
                );
                list.add(it);
            }
        } catch (Exception ex) {
            System.err.println("[InventoryDAO.getAll] " + ex.getMessage());
        }
        return list;
    }

    public static List<InventoryItem> getByType(InventoryType type) {
        List<InventoryItem> list = new ArrayList<>();
        String sql = "SELECT * FROM inventory WHERE type = ?";
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
                            InventoryType.valueOf(rs.getString("type"))
                    ));
                }
            }
        } catch (Exception ex) {
            System.err.println("[InventoryDAO.getByType] " + ex.getMessage());
        }
        return list;
    }

    public static void add(InventoryItem item) {
        String sql = "INSERT INTO inventory(name, quantity, price, type) VALUES (?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, item.getName());
            ps.setInt(2, item.getQuantity());
            ps.setDouble(3, item.getPrice());
            ps.setString(4, item.getType().name());
            ps.executeUpdate();
        } catch (Exception ex) {
            System.err.println("[InventoryDAO.add] " + ex.getMessage());
        }
    }

    public static void update(InventoryItem item) {
        String sql = "UPDATE inventory SET name=?, quantity=?, price=?, type=? WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, item.getName());
            ps.setInt(2, item.getQuantity());
            ps.setDouble(3, item.getPrice());
            ps.setString(4, item.getType().name());
            ps.setInt(5, item.getId());
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
}
