package com.app.inventory.models;

public class User {

    private int id;
    private String username;
    private String password;
    private Role role;

    // NEW: Inventory type/location the user can access
    private InventoryType assignedInventoryType;

    // Additional fields for auditing and management
    private String createdAt;
    private String lastLogin;
    private boolean isActive;
    private String createdBy;

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;

        // Default: Admin sees ALL; Staff must be assigned manually later
        this.assignedInventoryType = InventoryType.ALL;
        this.isActive = true;
    }

    public User(String username, String password, Role role, InventoryType assignedInventoryType) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.assignedInventoryType = assignedInventoryType;
        this.isActive = true;
    }

    // ------------------------
    // Getters & Setters
    // ------------------------

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) { this.password = password; }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) { this.role = role; }

    public InventoryType getAssignedInventoryType() {
        return assignedInventoryType;
    }

    public void setAssignedInventoryType(InventoryType assignedInventoryType) {
        this.assignedInventoryType = assignedInventoryType;
    }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
