package com.app.inventory.services;

import com.app.inventory.models.User;
import com.app.inventory.models.Role;
import com.app.inventory.repository.UserRepository;

public class AuthService {

    private final UserRepository userRepository = new UserRepository();

    /**
     * Login user by username and password.
     * Only ADMIN and STAFF roles are allowed.
     *
     * @param username the username
     * @param password the password
     * @return User if valid credentials and role, otherwise null
     */
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return null; // User not found
        }

        if (!user.getPassword().equals(password)) {
            return null; // Incorrect password
        }

        // Ensure only ADMIN and STAFF roles can login
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.STAFF) {
            return null; // Invalid role
        }

        return user;
    }
}
