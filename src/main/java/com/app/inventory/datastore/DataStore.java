package com.app.inventory.datastore;

import com.app.inventory.models.InventoryItem;
import com.app.inventory.models.InventoryType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataStore {

    // Make public static for controller access
    public static List<InventoryItem> inventory = new ArrayList<>();

    static {
        inventory.add(new InventoryItem(1, "Coca Cola", 50, (int) 70.0, InventoryType.RESTAURANT));
        inventory.add(new InventoryItem(2, "Tusker Lager", 120, (int) 200.0, InventoryType.CLUB));
        inventory.add(new InventoryItem(3, "Chips", 40, (int) 150.0, InventoryType.RESTAURANT));
        inventory.add(new InventoryItem(4, "Whiskey Shot", 80, (int) 300.0, InventoryType.CLUB));
    }

    public static List<InventoryItem> getAllInventory() {
        return new ArrayList<>(inventory);
    }

    public static List<InventoryItem> getInventoryByType(InventoryType type) {
        return inventory.stream()
                .filter(item -> item.getType() == type)
                .collect(Collectors.toList());
    }

    public static void addInventoryItem(InventoryItem item) {
        inventory.add(item);
    }

    public static void updateInventoryItem(InventoryItem updatedItem) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getId() == updatedItem.getId()) {
                inventory.set(i, updatedItem);
                break;
            }
        }
    }
}
