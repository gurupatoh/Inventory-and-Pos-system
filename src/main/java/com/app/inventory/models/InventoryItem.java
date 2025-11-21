package com.app.inventory.models;

public class InventoryItem {
    private int id;
    private String name;
    private int quantity;
    private int unit; // e.g., bottles, kg, pieces
    private InventoryType type; // CLUB or RESTAURANT
    private double price;



    public InventoryItem(int id, String name, int quantity, int unit, InventoryType type) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.type = type;
        this.price = price;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getUnit() { return unit; }
    public void setUnit(int unit) { this.unit = unit; }

    public InventoryType getType() { return type; }
    public void setType(InventoryType type) { this.type = type; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
