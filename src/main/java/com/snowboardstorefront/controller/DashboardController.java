/*
 * File: DashboardController.java
 * Description: Controller for customer, expert, and admin dashboard pages
 * Author: Dennis Feldbruegge
 * Date Created: July 18, 2026
 * Last Updated: July 18, 2026
 */

package com.snowboardstorefront.controller;

import com.snowboardstorefront.dao.ProfileDAO;
import com.snowboardstorefront.dao.UserDAO;
import com.snowboardstorefront.model.Profile;
import com.snowboardstorefront.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles dashboard page requests for all three user roles
 */
@Controller
public class DashboardController {

    private final UserDAO userDAO;
    private final ProfileDAO profileDAO;

    /**
     * Initializes the controller with user and profile database operations
     *
     * @param userDAO    data access object for the users table
     * @param profileDAO data access object for the profile table
     */
    public DashboardController(UserDAO userDAO, ProfileDAO profileDAO) {
        this.userDAO = userDAO;
        this.profileDAO = profileDAO;
    }

    // Spring maps GET /customer-dashboard to this method
    @GetMapping("/customer-dashboard")
    public String customerDashboard(HttpSession session, Model model) {

        // Check if the user is logged in by looking for their ID in the session
        // If user_id is missing, nobody is logged in - send them to the login page
        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        // Make sure the logged in user is actually a customer
        // If they are a different role, send them to their correct dashboard instead
        String role = (String) session.getAttribute("role");
        if (!"customer".equals(role)) {
            return redirectByRole(role);
        }

        // Fetch the logged in user's profile and account info from the database
        Profile profile = profileDAO.findByUserId(userId);
        User user = userDAO.findById(userId);

        // Put the data into the model so Thymeleaf can display it in the template
        model.addAttribute("profile", profile);
        model.addAttribute("user", user);

        // Tell Spring to render the customer-dashboard.html template
        return "customer-dashboard";
    }

    // Spring maps GET /expert-dashboard to this method
    @GetMapping("/expert-dashboard")
    public String expertDashboard(HttpSession session, Model model) {

        // Check if the user is logged in
        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        // Make sure the logged in user is actually an expert
        String role = (String) session.getAttribute("role");
        if (!"expert".equals(role)) {
            return redirectByRole(role);
        }

        // Fetch the logged in expert's profile and account info from the database
        Profile profile = profileDAO.findByUserId(userId);
        User user = userDAO.findById(userId);

        // Put the data into the model so Thymeleaf can display it in the template
        model.addAttribute("profile", profile);
        model.addAttribute("user", user);

        // Tell Spring to render the expert-dashboard.html template
        return "expert-dashboard";
    }

    // Spring maps GET /admin-dashboard to this method
    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpSession session, Model model) {

        // Check if the user is logged in
        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        // Make sure the logged in user is actually an admin
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return redirectByRole(role);
        }

        // Fetch the logged in admin's profile and account info from the database
        Profile profile = profileDAO.findByUserId(userId);
        User user = userDAO.findById(userId);

        // Put the data into the model so Thymeleaf can display it in the template
        model.addAttribute("profile", profile);
        model.addAttribute("user", user);

        // Tell Spring to render the admin-dashboard.html template
        return "admin-dashboard";
    }

    private String redirectByRole(String role) {
        if ("admin".equals(role)) {
            return "redirect:/admin-dashboard";
        } else if ("expert".equals(role)) {
            return "redirect:/expert-dashboard";
        } else {
            return "redirect:/customer-dashboard";
        }
    }
}
