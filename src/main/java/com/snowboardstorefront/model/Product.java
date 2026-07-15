/*
 * File: Product.java
 * Description: Model class for product information
 * Author: Zach Christianson
 * Date Created: July 9, 2026
 * Last Updated: July 9, 2026
 */

package com.snowboardstorefront.model;

import java.math.BigDecimal;

/**
 * Represents a product from the product table
 */
public class Product {

    // Unique ID for the product
    private int productId;

    // Category ID connected to the product
    private int categoryId;

    // Name of the product
    private String productName;

    // Product description shown on the storefront
    private String description;

    // Product price
    private BigDecimal price;

    // Number of products available
    private int stockQuantity;

    // Category name used for display
    private String categoryName;

    /**
     * Default constructor
     */
    public Product() {
    }

    /**
     * Creates a product object with product information
     *
     * @param productId unique product ID
     * @param categoryId category ID connected to the product
     * @param productName product name
     * @param description product description
     * @param price product price
     * @param stockQuantity number of products available
     * @param categoryName category name used for display
     */
    public Product(int productId, int categoryId, String productName, String description,
                   BigDecimal price, int stockQuantity, String categoryName) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.categoryName = categoryName;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
