package com.app.inventory.datastore;

import com.app.inventory.models.InventoryItem;
import com.app.inventory.models.InventoryType;

import java.util.ArrayList;
import java.util.List;

public class DataStore {
    public static List<InventoryItem> inventory = new ArrayList<>();

    static {
        inventory.add(new InventoryItem(1, "Coca Cola", 50, 70.0, InventoryType.RESTAURANT, 1, null));
        inventory.add(new InventoryItem(2, "Tusker Lager", 120, 200.0, InventoryType.CLUB, 1, null));
        inventory.add(new InventoryItem(3, "Chips", 40, 150.0, InventoryType.RESTAURANT, 1, null));
        inventory.add(new InventoryItem(4, "Whiskey Shot", 80, 300.0, InventoryType.CLUB, 1, null));
    }
}
