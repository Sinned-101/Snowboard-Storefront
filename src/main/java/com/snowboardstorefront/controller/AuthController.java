/*
 * File: AuthController.java
 * Description: Controller for registration, login, and logout requests
 * Author: Zach Christianson
 * Date Created: June 28, 2026
 * Last Updated: June 28, 2026
 */

package com.snowboardstorefront.controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.snowboardstorefront.dao.ProfileDAO;
import com.snowboardstorefront.dao.UserDAO;
import com.snowboardstorefront.model.Profile;
import com.snowboardstorefront.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Handles requests related to accounts, such as registration, login, and logout
 */
@Controller
public class AuthController {

    private final UserDAO userDAO;
    private final ProfileDAO profileDAO;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Initializes the controller with access to user and profile database operations
     *
     * @param userDAO data access object for the users table
     * @param profileDAO data access object for the profile table
     */
    public AuthController(UserDAO userDAO, ProfileDAO profileDAO) {
        this.userDAO = userDAO;
        this.profileDAO = profileDAO;
    }

    /**
     * Handles new customer account registration
     *
     * @param username username entered by the user
     * @param email email entered by the user
     * @param password password entered by the user
     * @param confirmPassword password confirmation entered by the user
     * @param firstName user's first name
     * @param lastName user's last name
     * @param phone user's phone number
     * @param addressLine user's street address
     * @param city user's city
     * @param state user's state
     * @param postalCode user's postal code
     * @param country user's country
     * @param bio user's biography or gear notes
     * @param redirectAttributes stores temporary error messages after redirects
     * @return redirect to the login page after successful registration
     */
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam("confirm_password") String confirmPassword,
            @RequestParam(required = false, name = "first_name") String firstName,
            @RequestParam(required = false, name = "last_name") String lastName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false, name = "address_line") String addressLine,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false, name = "postal_code") String postalCode,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String bio,
            RedirectAttributes redirectAttributes) {

        // Show an error if the password and confirm password fields do not match
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute(
                    "registrationError",
                    "Passwords do not match. Please try again."
            );
            return "redirect:/register";
        }

        // Show an error if the username or email is already being used
        if (userDAO.userExists(username, email)) {
            redirectAttributes.addFlashAttribute(
                    "registrationError",
                    "An account with that username or email already exists."
            );
            return "redirect:/register";
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        // Hash the password before saving it to the database
        user.setPasswordHash(passwordEncoder.encode(password));

        // Public registration only creates customer accounts
        // Expert and admin accounts would be created by an administrator
        user.setRole("customer");

        int userId = userDAO.createUser(user);

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

        profileDAO.createProfile(profile);

        return "redirect:/login";
    }

    /**
     * Handles user login
     *
     * @param loginIdentifier username or email entered by the user
     * @param password password entered by the user
     * @param session current user session
     * @param redirectAttributes stores temporary error messages after redirects
     * @return redirect to the correct dashboard if login succeeds
     */
    @PostMapping("/login")
    public String loginUser(
            @RequestParam("login_identifier") String loginIdentifier,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Look up the user by username or email
        User user = userDAO.findByUsernameOrEmail(loginIdentifier);

        // Show an error if no matching account is found
        if (user == null) {
            redirectAttributes.addFlashAttribute(
                    "loginError",
                    "No account was found with that username or email."
            );
            return "redirect:/login";
        }

        // Show an error if the password is wrong
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            redirectAttributes.addFlashAttribute(
                    "loginError",
                    "Incorrect password. Please try again."
            );
            return "redirect:/login";
        }

        // Save logged-in user information in the session
        session.setAttribute("user_id", user.getUserId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        if ("admin".equals(user.getRole())) {
            return "redirect:/admin-dashboard";
        } else if ("expert".equals(user.getRole())) {
            return "redirect:/expert-dashboard";
        } else {
            return "redirect:/customer-dashboard";
        }
    }

    /**
     * Logs out the current user by ending the session.
     *
     * @param session current user session
     * @return redirect to the login page after logout
     */
    @GetMapping("/logout")
    public String logoutUser(HttpSession session) {

        // End the current user session
        session.invalidate();

        return "redirect:/login";
    }
}
