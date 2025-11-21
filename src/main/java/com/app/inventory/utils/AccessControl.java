package com.app.inventory.utils;

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
}
