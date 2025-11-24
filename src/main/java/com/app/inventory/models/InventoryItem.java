package com.app.inventory.models;

public class InventoryItem {
    private int id;
    private String name;
    private int quantity;
    private double price;
    private InventoryType type;
    private int creatorId;
    private String createdAt;

    public InventoryItem(int id, String name, int quantity, double price, InventoryType type, int creatorId, String createdAt) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
    }

    public InventoryItem(String name, int quantity, double price, InventoryType type, int creatorId) {
        this(0, name, quantity, price, type, creatorId, null);
    }

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public InventoryType getType() { return type; }
    public void setType(InventoryType type) { this.type = type; }
    public int getCreatorId() { return creatorId; }
    public void setCreatorId(int creatorId) { this.creatorId = creatorId; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
