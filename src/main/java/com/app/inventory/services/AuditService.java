package com.app.inventory.services;

import com.app.inventory.dao.AuditDAO;
import com.app.inventory.models.AuditLog;
import com.app.inventory.models.User;
import com.app.inventory.models.Role;

import java.util.List;

public class AuditService {

    /**
     * Log user login
     */
    public static void logLogin(User user, String ip, String ua) {
        AuditDAO.logAudit(user.getId(), "LOGIN", "User logged in: " + user.getUsername(), ip, ua);
    }

    /**
     * Log user logout
     */
    public static void logLogout(User user, String ip, String ua) {
        AuditDAO.logAudit(user.getId(), "LOGOUT", "User logged out: " + user.getUsername(), ip, ua);
    }

    /**
     * Log inventory access (view)
     */
    public static void logInventoryAccess(User user, String inventoryType, String ip, String ua) {
        AuditDAO.logAudit(user.getId(), "INVENTORY_ACCESS", "Viewed inventory: " + inventoryType, ip, ua);
    }

    /**
     * Log inventory item added
     */
    public static void logInventoryAdd(User user, String itemName, String ip, String ua) {
        AuditDAO.logAudit(user.getId(), "INVENTORY_ADD", "Added item: " + itemName, ip, ua);
    }

    /**
     * Log inventory item updated
     */
    public static void logInventoryUpdate(User user, String itemName, String ip, String ua) {
        AuditDAO.logAudit(user.getId(), "INVENTORY_UPDATE", "Updated item: " + itemName, ip, ua);
    }

    /**
     * Log inventory item deleted
     */
    public static void logInventoryDelete(User user, String itemName, String ip, String ua) {
        AuditDAO.logAudit(user.getId(), "INVENTORY_DELETE", "Deleted item: " + itemName, ip, ua);
    }

    /**
     * Get all audit logs (for admin)
     */
    public static List<AuditLog> getAllAuditLogs(User admin) {
        if (admin == null || admin.getRole() != Role.ADMIN) {
            return null;
        }
        return AuditDAO.getAllAuditLogs();
    }

    /**
     * Get audit logs by user
     */
    public static List<AuditLog> getAuditLogsByUser(User admin, int userId) {
        if (admin == null || admin.getRole() != Role.ADMIN) {
            return null;
        }
        return AuditDAO.getAuditLogsByUser(userId);
    }

    /**
     * Get audit logs by action
     */
    public static List<AuditLog> getAuditLogsByAction(User admin, String action) {
        if (admin == null || admin.getRole() != Role.ADMIN) {
            return null;
        }
        return AuditDAO.getAuditLogsByAction(action);
    }

    // Add more log methods as needed
}
