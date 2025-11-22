package com.app.inventory.controllers;

import com.app.inventory.models.InventoryType;

/**
 * Controller for Restaurant Inventory management.
 * Forces the inventory view to show only RESTAURANT items.
 */
public class RestaurantController extends InventoryController {

    @Override
    public void initialize() {
        setFilterType(InventoryType.RESTAURANT);
        super.initialize();
    }
}
