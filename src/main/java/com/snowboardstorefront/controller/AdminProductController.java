/*
 * File: AdminProductController.java
 * Description: Controller for admin product management pages
 * Author: Dennis Feldbruegge
 * Date Created: July 18, 2026
 * Last Updated: July 18, 2026
 */

package com.snowboardstorefront.controller;

import com.snowboardstorefront.dao.CategoryDAO;
import com.snowboardstorefront.dao.ProductDAO;
import com.snowboardstorefront.model.Product;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * Handles admin product management — listing, adding, editing, and deleting products
 */
@Controller
public class AdminProductController {

    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;

    // Spring injects the ProductDAO and CategoryDAO through the constructor
    public AdminProductController(ProductDAO productDAO, CategoryDAO categoryDAO) {
        this.productDAO = productDAO;
        this.categoryDAO = categoryDAO;
    }

    // Shows the admin product management page with a full list of all products
    @GetMapping("/admin/products")
    public String adminProductsPage(HttpSession session, Model model) {

        // Only admins can access this page - redirect anyone else to login
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        // Fetch all products from the database and pass them to the template
        model.addAttribute("products", productDAO.findAllProducts());

        return "admin-products";
    }

    // Shows the form for adding a new product
    @GetMapping("/admin/products/add")
    public String showAddProductForm(HttpSession session, Model model) {

        // Only admins can access this page
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        // Load all categories so the admin can pick one from a dropdown
        model.addAttribute("categories", categoryDAO.findAllCategories());

        return "admin-product-add";
    }

    // Receives the add product form submission and inserts the new product into the database
    @PostMapping("/admin/products/add")
    public String addProduct(
            HttpSession session,
            @RequestParam int categoryId,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam BigDecimal price,
            @RequestParam int stockQuantity) {

        // Only admins can submit this form
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        // Build a Product object from the form values and insert it into the database
        Product product = new Product();
        product.setCategoryId(categoryId);
        product.setProductName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);

        productDAO.addProduct(product);

        // Redirect back to the product management page after saving
        return "redirect:/admin/products";
    }

    // Shows the edit form pre-filled with the selected product's current values
    @GetMapping("/admin/products/edit/{productId}")
    public String showEditProductForm(
            HttpSession session,
            @PathVariable int productId,
            Model model) {

        // Only admins can access this page
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        // Look up the product to pre-fill the form fields
        Product product = productDAO.findProductById(productId);
        if (product == null) {
            return "redirect:/admin/products";
        }

        // Pass the product and category list to the template
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryDAO.findAllCategories());

        return "admin-product-edit";
    }

    // Receives the edit form submission and updates the product in the database
    @PostMapping("/admin/products/edit/{productId}")
    public String editProduct(
            HttpSession session,
            @PathVariable int productId,
            @RequestParam int categoryId,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam BigDecimal price,
            @RequestParam int stockQuantity) {

        // Only admins can submit this form
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        // Build an updated Product object from the form values and save it to the database
        Product product = new Product();
        product.setProductId(productId);
        product.setCategoryId(categoryId);
        product.setProductName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);

        productDAO.updateProduct(product);

        // Redirect back to the product management page after saving
        return "redirect:/admin/products";
    }

    // Deletes the selected product from the database
    @PostMapping("/admin/products/delete/{productId}")
    public String deleteProduct(
            HttpSession session,
            @PathVariable int productId) {

        // Only admins can delete products
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        productDAO.deleteProduct(productId);

        // Redirect back to the product management page after deleting
        return "redirect:/admin/products";
    }
}
