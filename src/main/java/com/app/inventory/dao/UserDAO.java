package com.app.inventory.dao;

import com.app.inventory.db.Database;
import com.app.inventory.models.InventoryType;
import com.app.inventory.models.Role;
import com.app.inventory.models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple CRUD for users. Uses prepared statements to avoid SQL injection.
 * Passwords are stored hashed with BCrypt.
 */
public class UserDAO {

    public static void addUser(User user) {
        String sql = "INSERT INTO users(username, password, role, assigned_inventory_type) VALUES (?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12));
            ps.setString(1, user.getUsername());
            ps.setString(2, hashed);
            ps.setString(3, user.getRole().name());
            ps.setString(4, user.getAssignedInventoryType().name());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("[UserDAO.addUser] " + ex.getMessage());
        }
    }

    public static User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String pwdHash = rs.getString("password");
                    Role role = Role.valueOf(rs.getString("role"));
                    InventoryType type = InventoryType.valueOf(rs.getString("assigned_inventory_type"));
                    // Important: We do NOT return the hashed password as "password" field for auth
                    User u = new User(rs.getString("username"), pwdHash, role, type);
                    return u;
                }
            }
        } catch (Exception ex) {
            System.err.println("[UserDAO.findByUsername] " + ex.getMessage());
        }
        return null;
    }

    /**
     * Verifies credentials using BCrypt and returns a User with assigned inventory type (password field will be plaintext?).
     * We keep the stored hash in the User.password for verification; AuthService will call BCrypt.checkpw.
     */
    public static boolean verifyPassword(String username, String plainPassword) {
        User u = findByUsername(username);
        if (u == null) return false;
        String storedHash = u.getPassword();
        return BCrypt.checkpw(plainPassword, storedHash);
    }

    public static List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, username, role, assigned_inventory_type, created_at, last_login, is_active FROM users";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Note: password not included for security
                    User u = new User(rs.getString("username"), "", Role.valueOf(rs.getString("role")), InventoryType.valueOf(rs.getString("assigned_inventory_type")));
                    u.setId(rs.getInt("id"));
                    u.setCreatedAt(rs.getString("created_at"));
                    u.setLastLogin(rs.getString("last_login"));
                    u.setActive(rs.getInt("is_active") == 1);
                    list.add(u);
            }
        } catch (Exception ex) {
            System.err.println("[UserDAO.getAllUsers] " + ex.getMessage());
        }
        return list;
    }

    public static User getUserById(int id) {
        String sql = "SELECT id, username, role, assigned_inventory_type, created_at, last_login, is_active FROM users WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User(rs.getString("username"), "", Role.valueOf(rs.getString("role")), InventoryType.valueOf(rs.getString("assigned_inventory_type")));

                    return u;
                }
            }
        } catch (Exception ex) {
            System.err.println("[UserDAO.getUserById] " + ex.getMessage());
        }
        return null;
    }

    public static void updateUser(User user) {
        String sql = "UPDATE users SET username=?, role=?, assigned_inventory_type=? WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getRole().name());
            ps.setString(3, user.getAssignedInventoryType().name());
            ps.setInt(4, user.getId()); // TODO: need getId() in User
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("[UserDAO.updateUser] " + ex.getMessage());
        }
    }

    public static void deleteUserById(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception ex) {
            System.err.println("[UserDAO.deleteUserById] " + ex.getMessage());
        }
    }

    public static void adminResetPassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password=? WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
            ps.setString(1, hashed);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("[UserDAO.adminResetPassword] " + ex.getMessage());
        }
    }

    public static void updateLastLogin(String username) {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE username = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("[UserDAO.updateLastLogin] " + ex.getMessage());
        }
    }
}
