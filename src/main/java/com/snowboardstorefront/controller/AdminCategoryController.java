/*
 * File: AdminCategoryController.java
 * Description: Controller for admin category management - listing, adding, editing, and deleting categories
 * Author: Dennis Feldbruegge
 * Date Created: July 18, 2026
 * Last Updated: July 18, 2026
 */

package com.snowboardstorefront.controller;

import com.snowboardstorefront.dao.CategoryDAO;
import com.snowboardstorefront.model.Category;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles admin category management - listing, adding, editing, and deleting product categories
 */
@Controller
public class AdminCategoryController {

    private final CategoryDAO categoryDAO;

    // Spring injects the CategoryDAO through the constructor
    public AdminCategoryController(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    // Shows the admin category management page with all categories listed
    @GetMapping("/admin/categories")
    public String adminCategoriesPage(HttpSession session, Model model) {

        // Only admins can access this page
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        // Fetch all categories and pass them to the template
        model.addAttribute("categories", categoryDAO.findAllCategories());

        return "admin-categories";
    }

    // Shows the form for adding a new category
    @GetMapping("/admin/categories/add")
    public String showAddCategoryForm(HttpSession session) {

        // Only admins can access this page
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        return "admin-category-add";
    }

    // Receives the add category form and inserts the new category into the database
    @PostMapping("/admin/categories/add")
    public String addCategory(
            HttpSession session,
            @RequestParam String name,
            @RequestParam(required = false) String description) {

        // Only admins can submit this form
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        // Build a Category object from the form values and insert it
        Category category = new Category();
        category.setCategoryName(name);
        category.setDescription(description);

        categoryDAO.addCategory(category);

        // Redirect back to the category management page after saving
        return "redirect:/admin/categories";
    }

    // Shows the edit form pre-filled with the selected category's current values
    @GetMapping("/admin/categories/edit/{categoryId}")
    public String showEditCategoryForm(
            HttpSession session,
            @PathVariable int categoryId,
            Model model) {

        // Only admins can access this page
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        // Fetch the category to pre-fill the form fields
        Category category = categoryDAO.findCategoryById(categoryId);
        if (category == null) {
            return "redirect:/admin/categories";
        }

        model.addAttribute("category", category);

        return "admin-category-edit";
    }

    // Receives the edit form and updates the category in the database
    @PostMapping("/admin/categories/edit/{categoryId}")
    public String editCategory(
            HttpSession session,
            @PathVariable int categoryId,
            @RequestParam String name,
            @RequestParam(required = false) String description) {

        // Only admins can submit this form
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        // Build the updated Category object and save it
        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setCategoryName(name);
        category.setDescription(description);

        categoryDAO.updateCategory(category);

        // Redirect back to the category management page after saving
        return "redirect:/admin/categories";
    }

    // Deletes the selected category from the database
    @PostMapping("/admin/categories/delete/{categoryId}")
    public String deleteCategory(
            HttpSession session,
            @PathVariable int categoryId) {

        // Only admins can delete categories
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        categoryDAO.deleteCategory(categoryId);

        // Redirect back to the category management page after deleting
        return "redirect:/admin/categories";
    }
}
