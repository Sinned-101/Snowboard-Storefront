/*
 * File: InStorePickupChannel.java
 * Description: Order channel for in-store pickup orders. The customer places the order
 *              online and picks it up at the store. The order stays active and editable
 *              until it is marked as picked up by staff. Once picked up it cannot be changed.
 * Author: Dennis Feldbruegge
 * Date Created: August 16, 2026
 * Last Updated: August 16, 2026
 */

package com.snowboardstorefront.model;

// Pickup orders are placed online and collected at the store - editable until picked up
public class InStorePickupChannel extends OrderChannel {

    // True once staff marks the order as picked up by the customer
    private boolean pickedUp;

    public InStorePickupChannel() {
        super("in_store_pickup");
    }

    // Pickup orders are editable while still waiting to be picked up
    @Override
    public boolean isEditable(String status) {
        return !pickedUp && "pending".equals(status);
    }

    @Override
    public String getDisplayName() {
        return "In-Store Pickup";
    }

    public boolean isPickedUp() {
        return pickedUp;
    }

    public void setPickedUp(boolean pickedUp) {
        this.pickedUp = pickedUp;
    }
}
