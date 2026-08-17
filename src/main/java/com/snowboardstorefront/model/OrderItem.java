/*
 * File: OrderItem.java
 * Description: Model class for a single line item in a placed order
 * Author: Dennis Feldbruegge
 * Date Created: August 16, 2026
 * Last Updated: August 16, 2026
 */

package com.snowboardstorefront.model;

import java.math.BigDecimal;

// Represents one product line in an order - price is frozen at purchase time so history stays accurate
public class OrderItem {

    private int orderItemId;
    private int orderId;
    private int productId;
    private String productName;
    private int quantity;

    // The unit price at the time the order was placed - frozen at purchase time
    private BigDecimal priceAtOrder;

    public OrderItem() {}

    public OrderItem(int orderItemId, int orderId, int productId, String productName,
                     int quantity, BigDecimal priceAtOrder) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
    }

    // Computed line total - quantity multiplied by the price at order time
    public BigDecimal getLineTotal() {
        return priceAtOrder.multiply(BigDecimal.valueOf(quantity));
    }

    public int getOrderItemId() { return orderItemId; }
    public void setOrderItemId(int orderItemId) { this.orderItemId = orderItemId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getPriceAtOrder() { return priceAtOrder; }
    public void setPriceAtOrder(BigDecimal priceAtOrder) { this.priceAtOrder = priceAtOrder; }
}
