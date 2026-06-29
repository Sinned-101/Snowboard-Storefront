/*
 * File: HomeController.java
 * Description: Controller for basic page navigation in the Black Diamond Gear web application
 * Author: Zach Christianson
 * Date Created: June 25, 2026
 * Last Updated: June 28, 2026
 */

package com.snowboardstorefront.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles basic page navigation for the web application
 */
@Controller
public class HomeController {

    /**
     * Displays the home page when the user visits the main site URL
     *
     * @return the index template
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * Displays the registration page
     *
     * @return the register template
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /**
     * Displays the login page
     *
     * @return the login template
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}