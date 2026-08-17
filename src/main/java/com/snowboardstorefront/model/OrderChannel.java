/*
 * File: OrderChannel.java
 * Description: Abstract base class representing the channel through which an order was placed.
 *              Subclasses define behavior specific to delivery and in-store orders.
 * Author: Dennis Feldbruegge
 * Date Created: August 16, 2026
 * Last Updated: August 16, 2026
 */

package com.snowboardstorefront.model;

// Base class for all order channels - subclasses add channel-specific fields and edit rules
public abstract class OrderChannel {

    // Identifies which type of channel this order used
    private String channelType;

    public OrderChannel(String channelType) {
        this.channelType = channelType;
    }

    public String getChannelType() {
        return channelType;
    }

    // Subclasses define whether an order in this channel can be edited
    public abstract boolean isEditable(String status);

    // Convenience method used in Thymeleaf to display a readable label
    public String getDisplayName() {
        return "in_store".equals(channelType) ? "In Store" : "Delivery";
    }
}
