package com.app.inventory.services;

import com.app.inventory.dao.UserDAO;
import com.app.inventory.models.User;
import com.app.inventory.models.Role;
import java.util.List;
import java.util.Collections;

public class UserManagementService {

    /**
     * Create a new user (admin only, but not checked here - check in controller)
     * Assumes newUser has plain text password, DAO will hash it
     */
    public static boolean createUser(User newUser) {
        if (newUser == null || newUser.getUsername() == null || newUser.getPassword() == null) {
            return false;
        }
        // Check if username already exists
        if (UserDAO.findByUsername(newUser.getUsername()) != null) {
            return false;
        }
        UserDAO.addUser(newUser);
        return true;
    }

    /**
     * Get all users (admin only)
     */
    public static List<User> getAllUsers(User admin) {
        if (admin == null || admin.getRole() != Role.ADMIN) {
            return Collections.emptyList();
        }
        return UserDAO.getAllUsers();
    }

    /**
     * Add user (admin only) - wrapper around createUser with admin check
     */
    public static boolean addUser(User admin, User newUser, String ip, String app) {
        if (admin == null || admin.getRole() != Role.ADMIN) {
            return false;
        }
        if (!createUser(newUser)) {
            return false;
        }
        // Log audit
        AuditService.logUserCreate(admin, newUser.getUsername(), ip, app);
        return true;
    }

    /**
     * Delete user (admin only)
     */
    public static boolean deleteUser(User admin, int id, String ip, String app) {
        if (admin == null || admin.getRole() != Role.ADMIN || admin.getId() == id) {
            // Prevent deleting self
            return false;
        }
        // Check if user exists
        User userToDelete = UserDAO.getUserById(id);
        if (userToDelete == null) {
            return false;
        }
        // Log audit before deletion
        AuditService.logUserDelete(admin, userToDelete.getUsername(), ip, app);
        UserDAO.deleteUserById(id);
        return true;
    }

    /**
     * Admin reset password for a user (admin only)
     */
    public static boolean adminResetPassword(User admin, int id, String newPassword, String ip, String app) {
        if (admin == null || admin.getRole() != Role.ADMIN || newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }
        // Check if user exists
        if (UserDAO.getUserById(id) == null) {
            return false;
        }
        UserDAO.adminResetPassword(id, newPassword);
        // Log audit
        AuditService.logUserResetPassword(admin, UserDAO.getUserById(id).getUsername(), ip, app);
        return true;
    }
}
