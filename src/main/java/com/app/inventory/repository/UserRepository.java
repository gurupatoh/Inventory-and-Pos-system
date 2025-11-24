package com.app.inventory.repository;

import com.app.inventory.models.InventoryType;
import com.app.inventory.models.Role;
import com.app.inventory.models.User;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private static final Map<String, User> users = new HashMap<>();

    static {
        // supply InventoryType for constructor
        users.put("admin", new User("admin", "admin123", Role.ADMIN, InventoryType.ALL));
        users.put("staff", new User("staff", "staff123", Role.STAFF, InventoryType.CLUB));
    }

    public static Map<String, User> getUsers() {
        return users;
    }
}
