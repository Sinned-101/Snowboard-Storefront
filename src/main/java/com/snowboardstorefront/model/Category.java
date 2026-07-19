/*
 * File: Category.java
 * Description: Model class for product category information
 * Author: Zach Christianson
 * Date Created: July 9, 2026
 * Last Updated: July 9, 2026
 */

package com.snowboardstorefront.model;

/**
 * Represents a category from the category table
 */
public class Category {

    // Unique ID for the category
    private int categoryId;

    // Name of the category
    private String categoryName;

    /**
     * Default constructor
     */
    public Category() {
    }

    /**
     * Creates a category object with category information
     *
     * @param categoryId unique category ID
     * @param categoryName category name
     */
    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    // Optional description field for the category
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
