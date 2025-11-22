package com.app.inventory.auth;

import com.app.inventory.dao.SessionDAO;
import com.app.inventory.models.User;

public class SessionManager {
    private static User user;

    public static void setUser(User u) {
        user = u;
        if (u != null) {
            SessionDAO.saveSession(u.getUsername());
        }
    }

    public static User getUser() {
        return user;
    }

    public static void clearSession() {
        user = null;
        SessionDAO.clear();
    }
}
