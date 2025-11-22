package com.app.inventory.services;

import com.app.inventory.dao.AuditDAO;
import com.app.inventory.dao.UserDAO;
import com.app.inventory.models.User;
import com.app.inventory.models.Role;
import com.app.inventory.models.InventoryType;

import java.util.List;

public class UserManagementService {

    /**
     * Add a new user. Only admin can do this.
     * Validates uniqueness, hashes password, logs audit.
     */
    public static boolean addUser(User admin, User newUser, String ip, String ua) {
        // Validation: only admin
        if (admin == null || admin.getRole() != Role.ADMIN) {
            return false;
        }

        // Validation: username not empty, unique
        if (newUser.getUsername() == null || newUser.getUsername().trim().isEmpty()) {
            return false;
        }

        // Check if username exists
        if (UserDAO.findByUsername(newUser.getUsername()) != null) {
            return false;
        }

        // Validation: password strength (simple check)
        if (newUser.getPassword() == null || newUser.getPassword().length() < 6) {
            return false;
        }

        // Validation: for staff, must assign CLUB or RESTAURANT
        if (newUser.getRole() == Role.STAFF) {
            if (newUser.getAssignedInventoryType() != InventoryType.CLUB &&
                newUser.getAssignedInventoryType() != InventoryType.RESTAURANT) {
                return false;
            }
        }

        // Set createdBy
        newUser.setCreatedBy(admin.getUsername());

        // Add user
        UserDAO.addUser(newUser);

        // Log audit
        AuditDAO.logAudit(admin.getId(), "USER_CREATED", "Created user: " + newUser.getUsername() + " (" + newUser.getRole() + ")", ip, ua);

        return true;
    }

    /**
     * Update user details (role, inventory type). Only admin.
     */
    public static boolean updateUser(User admin, User updatedUser, String ip, String ua) {
        if (admin == null || admin.getRole() != Role.ADMIN) {
            return false;
        }

        User existing = UserDAO.getUserById(updatedUser.getId());
        if (existing == null) {
            return false;
        }

        // Prevent updating own role?
        // For now, allow

        // Validation: for staff, inventory type
        if (updatedUser.getRole() == Role.STAFF) {
            if (updatedUser.getAssignedInventoryType() != InventoryType.CLUB &&
                updatedUser.getAssignedInventoryType() != InventoryType.RESTAURANT) {
                return false;
            }
        }

        UserDAO.updateUser(updatedUser);

        // Log audit
        AuditDAO.logAudit(admin.getId(), "USER_UPDATED", "Updated user: " + updatedUser.getUsername(), ip, ua);

        return true;
    }

    /**
     * Delete user by ID. Only admin. Cannot delete self or other admins?
     */
    public static boolean deleteUser(User admin, int userId, String ip, String ua) {
        if (admin == null || admin.getRole() != Role.ADMIN) {
            return false;
        }

        if (admin.getId() == userId) {
            return false; // Cannot delete self
        }

        // Check if user exists
        User target = UserDAO.getUserById(userId);
        if (target == null) {
            return false;
        }

        // Cannot delete other admins
        if (target.getRole() == Role.ADMIN) {
            return false;
        }

        UserDAO.deleteUserById(userId);

        // Log audit
        AuditDAO.logAudit(admin.getId(), "USER_DELETED", "Deleted user: " + target.getUsername(), ip, ua);

        return true;
    }

    /**
     * Admin reset user password.
     */
    public static boolean adminResetPassword(User admin, int userId, String newPassword, String ip, String ua) {
        if (admin == null || admin.getRole() != Role.ADMIN) {
            return false;
        }

        User target = UserDAO.getUserById(userId);
        if (target == null) {
            return false;
        }

        UserDAO.adminResetPassword(userId, newPassword);

        // Log audit
        AuditDAO.logAudit(admin.getId(), "PASSWORD_RESET", "Password reset for user: " + target.getUsername(), ip, ua);

        return true;
    }

    /**
     * Get all users for admin management.
     */
    public static List<User> getAllUsers(User admin) {
        if (admin == null || admin.getRole() != Role.ADMIN) {
            return null;
        }
        return UserDAO.getAllUsers();
    }

    /**
     * Get user by ID.
     */
    public static User getUserById(User admin, int id) {
        if (admin == null || admin.getRole() != Role.ADMIN) {
            return null;
        }
        return UserDAO.getUserById(id);
    }
}
