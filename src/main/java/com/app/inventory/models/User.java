package com.app.inventory.models;

public class User {
    private int id;
    private String username;
    private String password; // stored as hash in DB; in-memory may contain hash
    private Role role;
    private InventoryType assignedInventoryType;
    private String createdAt;
    private String lastLogin;
    private boolean active;

    public User(String username, String password, Role role, InventoryType assignedInventoryType) {
        this(0, username, password, role, assignedInventoryType, null, null, true);
    }

    public User(int id, String username, String password, Role role, InventoryType assignedInventoryType,
                String createdAt, String lastLogin, boolean active) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.assignedInventoryType = assignedInventoryType;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.active = active;
    }

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public InventoryType getAssignedInventoryType() { return assignedInventoryType; }
    public void setAssignedInventoryType(InventoryType assignedInventoryType) { this.assignedInventoryType = assignedInventoryType; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role) { this.role = role; }
}
