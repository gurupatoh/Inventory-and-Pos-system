package com.app.inventory.models;

public class InventoryItem {
    private int id;
    private String name;
    private int quantity;
    private InventoryType type; // CLUB or RESTAURANT
    private double price;
    private String drinkCategory; // Beer, Spirits, SoftDrinks, Other
    private int creatorId; // ID of the user who created this item
    private String createdAt; // Timestamp of creation

    public InventoryItem(int id, String name, int quantity, double price, InventoryType type) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
    }

    public InventoryItem(int id, String name, int quantity, double price, InventoryType type, String drinkCategory) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.drinkCategory = drinkCategory;
    }

    public InventoryItem(int id, String name, int quantity, double price, InventoryType type, String drinkCategory, int creatorId) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.drinkCategory = drinkCategory;
        this.creatorId = creatorId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public InventoryType getType() { return type; }
    public void setType(InventoryType type) { this.type = type; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDrinkCategory() { return drinkCategory; }
    public void setDrinkCategory(String drinkCategory) { this.drinkCategory = drinkCategory; }

    public int getCreatorId() { return creatorId; }
    public void setCreatorId(int creatorId) { this.creatorId = creatorId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    /**
     * Computed total price (price * quantity)
     */
    public double getTotalPrice() { return price * quantity; }
}
