/*
 * File: HomeController.java
 * Description: Controller for basic page navigation in the Black Diamond Gear web application
 * Author: Zach Christianson
 * Date Created: June 25, 2026
 * Last Updated: June 25, 2026
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
}