package com.app.inventory.utils;

import com.app.inventory.models.InventoryType;
import com.app.inventory.models.Role;
import com.app.inventory.models.User;

public class AccessControl {

    private final User currentUser;

    public AccessControl(User user) {
        this.currentUser = user;
    }

    public boolean isAdmin() {
        return currentUser.getRole() == Role.ADMIN;
    }

    public boolean isStaff() {
        return currentUser.getRole() == Role.STAFF;
    }

    public boolean canAccessAdminPage() {
        return isAdmin();
    }

    /**
     * Check if user can view/create inventory for a specific type.
     * Admin can view any type.
     * Staff can only view their assigned type.
     */
    public boolean canAccessInventory(InventoryType type) {
        if (isAdmin()) {
            return true;
        }
        // Staff can only access their assigned inventory type
        return isStaff() && currentUser.getAssignedInventoryType() == type;
    }

    /**
     * Check if user can modify (edit/delete) a specific inventory item.
     * Staff can modify items of their assigned type (admin can modify any).
     */
    public boolean canModifyInventoryItem(InventoryType itemType) {
        if (isAdmin()) {
            return true;
        }
        return currentUser.getAssignedInventoryType() == itemType;
    }

    /**
     * Get the allowed inventory type for this user.
     * Returns null if staff not assigned.
     */
    public InventoryType getAccessibleInventoryType() {
        if (isAdmin()) {
            return null; // Admin can access all
        }
        return currentUser.getAssignedInventoryType();
    }
}
