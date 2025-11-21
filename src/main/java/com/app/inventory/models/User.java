package com.app.inventory.models;

public class User {

    private String username;
    private String password;
    private Role role;

    // NEW: Inventory type/location the user can access
    private InventoryType assignedInventoryType;

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;

        // Default: Admin sees ALL; Staff must be assigned manually later
        this.assignedInventoryType = InventoryType.ALL;
    }

    public User(String username, String password, Role role, InventoryType assignedInventoryType) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.assignedInventoryType = assignedInventoryType;
    }

    // ------------------------
    // Getters & Setters
    // ------------------------

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public InventoryType getAssignedInventoryType() {
        return assignedInventoryType;
    }

    public void setAssignedInventoryType(InventoryType assignedInventoryType) {
        this.assignedInventoryType = assignedInventoryType;
    }
}
