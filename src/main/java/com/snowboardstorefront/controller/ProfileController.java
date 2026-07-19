/*
 * File: ProfileController.java
 * Description: Controller for the edit profile page - all roles can edit their own profile
 * Author: Dennis Feldbruegge
 * Date Created: July 18, 2026
 * Last Updated: July 18, 2026
 */

package com.snowboardstorefront.controller;

import com.snowboardstorefront.dao.ProfileDAO;
import com.snowboardstorefront.model.Profile;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles profile editing for all logged-in users
 */
@Controller
public class ProfileController {

    private final ProfileDAO profileDAO;

    // Spring injects the ProfileDAO through the constructor
    public ProfileController(ProfileDAO profileDAO) {
        this.profileDAO = profileDAO;
    }

    // Shows the edit profile page pre-filled with the user's current profile data
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {

        // Redirect to login if no one is logged in
        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        // Fetch the logged-in user's profile and pass it to the template
        Profile profile = profileDAO.findByUserId(userId);
        model.addAttribute("profile", profile);

        return "profile";
    }

    // Receives the edit profile form and saves the updated profile to the database
    @PostMapping("/profile")
    public String updateProfile(
            HttpSession session,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false, name = "address_line") String addressLine,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false, name = "postal_code") String postalCode,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String bio) {

        // Redirect to login if no one is logged in
        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        // Build the updated profile object from the submitted form values
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

        // Save the updated profile to the database
        profileDAO.updateProfile(profile);

        // Redirect back to the correct dashboard based on the user's role
        String role = (String) session.getAttribute("role");
        if ("admin".equals(role)) return "redirect:/admin-dashboard";
        if ("expert".equals(role)) return "redirect:/expert-dashboard";
        return "redirect:/customer-dashboard";
    }
}
