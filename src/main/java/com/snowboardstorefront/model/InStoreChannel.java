/*
 * File: InStoreChannel.java
 * Description: Order channel for in-store purchases. In-store orders are
 *              completed instantly and cannot be edited or cancelled.
 * Author: Dennis Feldbruegge
 * Date Created: August 16, 2026
 * Last Updated: August 16, 2026
 */

package com.snowboardstorefront.model;

// In-store orders complete at the point of sale and are never editable
public class InStoreChannel extends OrderChannel {

    public InStoreChannel() {
        super("in_store");
    }

    // In-store orders are finalized at the point of sale and cannot be changed
    @Override
    public boolean isEditable(String status) {
        return false;
    }
}
