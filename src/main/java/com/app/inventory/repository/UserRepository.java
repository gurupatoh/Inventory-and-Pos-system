package com.app.inventory.repository;

import com.app.inventory.models.User;
import com.app.inventory.models.Role;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    // Mock database
    private static final Map<String, User> users = new HashMap<>();

    static {
        // username, password, role
        users.put("admin", new User("admin", "admin123", Role.ADMIN));
        users.put("staff", new User("staff", "staff123", Role.STAFF));
    }

    public User findByUsername(String username) {
        return users.get(username);
    }
}
