/*
 * File: CartItem.java
 * Description: Model class for cart item information
 * Author: Zach Christianson
 * Date Created: July 23, 2026
 * Last Updated: July 23, 2026
 */

package com.snowboardstorefront.model;

import java.math.BigDecimal;

/**
 * Represents an item in a user's shopping cart
 */
public class CartItem {

    // Unique ID for the cart item
    private int cartItemId;

    // Cart ID connected to the item
    private int cartId;

    // Product ID connected to the item
    private int productId;

    // Product name shown on the cart page
    private String productName;

    // Product price from the product table
    private BigDecimal price;

    // Quantity of the product in the cart
    private int quantity;

    // Total price for this cart item
    private BigDecimal lineTotal;

    /**
     * Default constructor
     */
    public CartItem() {
    }

    /**
     * Creates a cart item object with product and cart information
     *
     * @param cartItemId unique cart item ID
     * @param cartId cart ID connected to the item
     * @param productId product ID connected to the item
     * @param productName product name shown on the cart page
     * @param price product price from the product table
     * @param quantity quantity of the product in the cart
     * @param lineTotal total price for this cart item
     */
    public CartItem(int cartItemId, int cartId, int productId, String productName,
                    BigDecimal price, int quantity, BigDecimal lineTotal) {
        this.cartItemId = cartItemId;
        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.lineTotal = lineTotal;
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
}
