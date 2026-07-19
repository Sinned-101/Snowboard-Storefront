/*
 * File: PasswordController.java
 * Description: Controller for the change password page - all roles can change their own password
 * Author: Dennis Feldbruegge
 * Date Created: July 18, 2026
 * Last Updated: July 18, 2026
 */

package com.snowboardstorefront.controller;

import com.snowboardstorefront.dao.UserDAO;
import com.snowboardstorefront.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles password changes for all logged-in users
 */
@Controller
public class PasswordController {

    private final UserDAO userDAO;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Spring injects the UserDAO through the constructor
    public PasswordController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // Shows the change password page
    @GetMapping("/change-password")
    public String changePasswordPage(HttpSession session) {

        // Redirect to login if no one is logged in
        if (session.getAttribute("user_id") == null) {
            return "redirect:/login";
        }

        return "change-password";
    }

    // Receives the change password form and updates the password if the current one is correct
    @PostMapping("/change-password")
    public String changePassword(
            HttpSession session,
            @RequestParam("current_password") String currentPassword,
            @RequestParam("new_password") String newPassword,
            @RequestParam("confirm_password") String confirmPassword,
            RedirectAttributes redirectAttributes) {

        // Redirect to login if no one is logged in
        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        // Make sure the new password and confirmation match before doing anything
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("passwordError", "New passwords do not match.");
            return "redirect:/change-password";
        }

        // Look up the current user to get their stored password hash
        User user = userDAO.findById(userId);

        // Verify that the current password they entered is correct
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            redirectAttributes.addFlashAttribute("passwordError", "Current password is incorrect.");
            return "redirect:/change-password";
        }

        // Hash the new password and save it to the database
        String newHash = passwordEncoder.encode(newPassword);
        userDAO.updatePassword(userId, newHash);

        // Redirect back to the correct dashboard with a success message
        redirectAttributes.addFlashAttribute("passwordSuccess", "Password updated successfully.");
        String role = (String) session.getAttribute("role");
        if ("admin".equals(role)) return "redirect:/admin-dashboard";
        if ("expert".equals(role)) return "redirect:/expert-dashboard";
        return "redirect:/customer-dashboard";
    }
}
