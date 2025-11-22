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
 * Handles CRUD operations for User.
 * Uses BCrypt hashing and prepared statements for security.
 */
public class UserDAO {

    /** CREATE USER */
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

    /** GET USER BY USERNAME */
    public static User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    String pwdHash = rs.getString("password");
                    Role role = Role.valueOf(rs.getString("role"));

                    InventoryType type;
                    try {
                        type = InventoryType.valueOf(rs.getString("assigned_inventory_type"));
                    } catch (Exception e) {
                        type = InventoryType.ALL;
                        System.err.println("[UserDAO.findByUsername] Invalid inventory type → default ALL");
                    }

                    User u = new User(
                            rs.getString("username"),
                            pwdHash, // hashed password for BCrypt verification
                            role,
                            type
                    );

                    u.setId(rs.getInt("id"));
                    u.setActive(rs.getInt("is_active") == 1);
                    u.setCreatedAt(rs.getString("created_at"));
                    u.setLastLogin(rs.getString("last_login"));

                    return u;
                }
            }

        } catch (SQLException ex) {
            System.err.println("[UserDAO.findByUsername] " + ex.getMessage());
        }

        return null;
    }

    /** PASSWORD CHECK WITH BCRYPT */
    public static boolean verifyPassword(String username, String plainPassword) {
        User u = findByUsername(username);
        if (u == null) return false;

        return BCrypt.checkpw(plainPassword, u.getPassword());
    }

    /** GET ALL USERS */
    public static List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Role role = Role.valueOf(rs.getString("role"));

                InventoryType type;
                try {
                    type = InventoryType.valueOf(rs.getString("assigned_inventory_type"));
                } catch (Exception e) {
                    type = InventoryType.ALL;
                }

                User u = new User(
                        rs.getString("username"),
                        "", // Do NOT expose password
                        role,
                        type
                );

                u.setId(rs.getInt("id"));
                u.setActive(rs.getInt("is_active") == 1);
                u.setCreatedAt(rs.getString("created_at"));
                u.setLastLogin(rs.getString("last_login"));

                list.add(u);
            }

        } catch (SQLException ex) {
            System.err.println("[UserDAO.getAllUsers] " + ex.getMessage());
        }

        return list;
    }

    /** GET USER BY ID — FIXED VERSION */
    public static User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    Role role = Role.valueOf(rs.getString("role"));

                    InventoryType type;
                    try {
                        type = InventoryType.valueOf(rs.getString("assigned_inventory_type"));
                    } catch (Exception e) {
                        type = InventoryType.ALL;
                    }

                    User u = new User(
                            rs.getString("username"),
                            "", // do NOT return password hash
                            role,
                            type
                    );

                    u.setId(rs.getInt("id"));
                    u.setActive(rs.getInt("is_active") == 1);
                    u.setCreatedAt(rs.getString("created_at"));
                    u.setLastLogin(rs.getString("last_login"));

                    return u;
                }
            }

        } catch (SQLException ex) {
            System.err.println("[UserDAO.getUserById] " + ex.getMessage());
        }

        return null;
    }

    /** UPDATE USER DETAILS */
    public static void updateUser(User user) {
        String sql = "UPDATE users SET username=?, role=?, assigned_inventory_type=? WHERE id=?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getRole().name());
            ps.setString(3, user.getAssignedInventoryType().name());
            ps.setInt(4, user.getId());

            ps.executeUpdate();

        } catch (SQLException ex) {
            System.err.println("[UserDAO.updateUser] " + ex.getMessage());
        }
    }

    /** DELETE USER */
    public static void deleteUserById(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException ex) {
            System.err.println("[UserDAO.deleteUserById] " + ex.getMessage());
        }
    }

    /** ADMIN RESETS PASSWORD FOR A STAFF */
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

    /** UPDATE LAST LOGIN TIMESTAMP */
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
