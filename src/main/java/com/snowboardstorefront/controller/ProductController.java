/*
 * File: ProductController.java
 * Description: Controller for product listing and product detail pages
 * Author: Zach Christianson
 * Date Created: July 13, 2026
 * Last Updated: July 14, 2026
 */

package com.snowboardstorefront.controller;

import com.snowboardstorefront.dao.CategoryDAO;
import com.snowboardstorefront.dao.ProductDAO;
import com.snowboardstorefront.model.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles product page requests for the storefront
 */
@Controller
public class ProductController {

    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;

    /**
     * Initializes the controller with product and category database operations
     *
     * @param productDAO data access object for the product table
     * @param categoryDAO data access object for the category table
     */
    public ProductController(ProductDAO productDAO, CategoryDAO categoryDAO) {
        this.productDAO = productDAO;
        this.categoryDAO = categoryDAO;
    }

    /**
     * Displays the product listing page
     *
     * @param category selected category name from the URL
     * @param model stores product and category data for the page
     * @return the products template
     */
    @GetMapping("/products")
    public String productsPage(
            @RequestParam(required = false) String category,
            Model model) {

        // Add all categories from the database to the page
        model.addAttribute("categories", categoryDAO.findAllCategories());

        if (category != null && !category.isBlank()) {

            // Show products from the selected category
            model.addAttribute("products", productDAO.findProductsByCategory(category));
            model.addAttribute("selectedCategory", category);
        } else {

            // Show all products when no category is selected
            model.addAttribute("products", productDAO.findAllProducts());
            model.addAttribute("selectedCategory", "All Products");
        }

        return "products";
    }

    /**
     * Displays one product detail page
     *
     * @param productId ID number from the URL for the selected product
     * @param model stores product data for the page
     * @return the product-details template
     */
    @GetMapping("/products/{productId}")
    public String productDetailsPage(@PathVariable int productId, Model model) {

        // Look up the selected product by its ID
        Product product = productDAO.findProductById(productId);

        if (product == null) {
            return "redirect:/products";
        }

        model.addAttribute("product", product);

        return "product-details";
    }
}
