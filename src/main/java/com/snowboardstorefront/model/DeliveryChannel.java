/*
 * File: DeliveryChannel.java
 * Description: Order channel for delivery orders. Holds shipping address,
 *              tracking number, and shipped date. Delivery orders can be edited
 *              until they are marked as shipped.
 * Author: Dennis Feldbruegge
 * Date Created: August 16, 2026
 * Last Updated: August 16, 2026
 */

package com.snowboardstorefront.model;

import java.sql.Timestamp;

// Delivery orders ship to a customer address and are editable until marked as shipped
public class DeliveryChannel extends OrderChannel {

    // The shipping address provided at checkout
    private String deliveryAddress;

    // Tracking number assigned when the order ships - null until shipped
    private String trackingNumber;

    // The date and time the order was marked as shipped - null until shipped
    private Timestamp shippedDate;

    public DeliveryChannel() {
        super("delivery");
    }

    // Delivery orders can only be edited while still pending - once shipped they are locked
    @Override
    public boolean isEditable(String status) {
        return "pending".equals(status);
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Timestamp getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Timestamp shippedDate) {
        this.shippedDate = shippedDate;
    }
}
