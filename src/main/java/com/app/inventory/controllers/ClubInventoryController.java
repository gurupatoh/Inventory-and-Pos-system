package com.app.inventory.controllers;

import com.app.inventory.models.InventoryType;

/**
 * Controller for Club Inventory management.
 * Forces the inventory view to show only CLUB items.
 */
public class ClubInventoryController extends InventoryController {

    @Override
    public void initialize() {
        setFilterType(InventoryType.CLUB);
        super.initialize();
    }
}
