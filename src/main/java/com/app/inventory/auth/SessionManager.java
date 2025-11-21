package com.app.inventory.auth;

import com.app.inventory.models.InventoryType;
import com.app.inventory.models.User;

public class SessionManager {

    private static User currentUser;

    public static void setUser(User user) {
        currentUser = user;
    }

    public static User getUser() {
        return currentUser;
    }

    public static void clearSession() {
        currentUser = null;
    }

    // Utility method to get the assigned inventory type for the current user
    public static InventoryType getAssignedInventoryType() {
        if (currentUser != null) {
            return currentUser.getAssignedInventoryType();
        }
        return null;
    }
}
