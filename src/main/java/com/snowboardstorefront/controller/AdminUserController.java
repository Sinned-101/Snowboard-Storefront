/*
 * File: AdminUserController.java
 * Description: Controller for admin user management - list all users, edit profiles, change roles
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles admin user management - listing users, editing their profiles, and changing roles
 */
@Controller
public class AdminUserController {

    private final UserDAO userDAO;
    private final ProfileDAO profileDAO;

    // Spring injects the UserDAO and ProfileDAO through the constructor
    public AdminUserController(UserDAO userDAO, ProfileDAO profileDAO) {
        this.userDAO = userDAO;
        this.profileDAO = profileDAO;
    }

    // Shows the admin user list page with all registered accounts
    @GetMapping("/admin/users")
    public String adminUsersPage(HttpSession session, Model model) {

        // Only admins can access this page
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        // Fetch all user accounts and pass them to the template
        model.addAttribute("users", userDAO.findAllUsers());

        return "admin-users";
    }

    // Shows the edit form pre-filled with the selected user's current profile and role
    @GetMapping("/admin/users/edit/{userId}")
    public String showEditUserForm(
            HttpSession session,
            @PathVariable int userId,
            Model model) {

        // Only admins can access this page
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        // Fetch the user account and their profile to pre-fill the form
        User user = userDAO.findById(userId);
        Profile profile = profileDAO.findByUserId(userId);

        if (user == null) {
            return "redirect:/admin/users";
        }

        model.addAttribute("user", user);
        model.addAttribute("profile", profile);

        return "admin-user-edit";
    }

    // Receives the edit form and saves the updated profile and role to the database
    @PostMapping("/admin/users/edit/{userId}")
    public String editUser(
            HttpSession session,
            @PathVariable int userId,
            @RequestParam String role,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false, name = "address_line") String addressLine,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false, name = "postal_code") String postalCode,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String bio) {

        // Only admins can submit this form
        String adminRole = (String) session.getAttribute("role");
        if (!"admin".equals(adminRole)) {
            return "redirect:/login";
        }

        // Update the user's role in the users table
        userDAO.updateRole(userId, role);

        // Build and save the updated profile
        Profile profile = new Profile();
        profile.setUserId(userId);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setPhone(phone);
        profile.setAddressLine(addressLine);
        profile.setCity(city);
        profile.setState(state);
        profile.setPostalCode(postalCode);
        profile.setCountry(country);
        profile.setBio(bio);

        profileDAO.updateProfile(profile);

        // Redirect back to the user list after saving
        return "redirect:/admin/users";
    }
}
