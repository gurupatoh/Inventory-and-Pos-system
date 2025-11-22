package com.app.inventory.services;

import com.app.inventory.dao.UserDAO;
import com.app.inventory.models.User;
import com.app.inventory.services.AuditService;

public class AuthService {

    public User login(String username, String password, String ip, String ua) {
        // find user record
        User stored = UserDAO.findByUsername(username);
        if (stored == null) return null;

        // verify password with bcrypt
        boolean ok = UserDAO.verifyPassword(username, password);
        if (!ok) return null;

        // Update last login
        UserDAO.updateLastLogin(username);

        // Log audit
        AuditService.logLogin(stored, ip, ua);

        // Return a safe user object — stored currently has hashed password in password field.
        // If you prefer, create a new User with same details but password=null.
        User result = new User(stored.getUsername(), "", stored.getRole(), stored.getAssignedInventoryType());
        result.setId(stored.getId()); // Set id for session
        return result;
    }
}
