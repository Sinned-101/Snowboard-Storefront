/*
 * File: Order.java
 * Description: Model class for a customer order record
 * Author: Dennis Feldbruegge
 * Date Created: August 16, 2026
 * Last Updated: August 16, 2026
 */

package com.snowboardstorefront.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

// Represents a placed order - holds the order details and an OrderChannel that describes how it was placed
public class Order {

    private int orderId;
    private int userId;
    private String status;
    private BigDecimal totalAmount;
    private Timestamp orderDate;

    // Either a DeliveryChannel or InStoreChannel - determined by the channel column
    private OrderChannel channel;

    public Order() {}

    public Order(int orderId, int userId, String status, BigDecimal totalAmount,
                 Timestamp orderDate, OrderChannel channel) {
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.channel = channel;
    }

    // Convenience method - delegates to the channel to check if this order can be edited
    public boolean isEditable() {
        return channel != null && channel.isEditable(status);
    }

    // Active orders = pending delivery OR pending in-store pickup - completed/in-store are not active
    public boolean isActive() {
        String channelType = channel.getChannelType();
        return ("delivery".equals(channelType) || "in_store_pickup".equals(channelType))
               && "pending".equals(status);
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

    public OrderChannel getChannel() { return channel; }
    public void setChannel(OrderChannel channel) { this.channel = channel; }

    // Customer username - populated by admin queries that JOIN with the users table
    private String customerUsername;

    public String getCustomerUsername() { return customerUsername; }
    public void setCustomerUsername(String customerUsername) { this.customerUsername = customerUsername; }
}
