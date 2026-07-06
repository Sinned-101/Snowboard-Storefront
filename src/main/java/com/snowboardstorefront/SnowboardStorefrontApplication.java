/*
 * File: SnowboardStorefrontApplication.java
 * Description: Main application class for the Snowboard Storefront Spring Boot application
 * Author: Zach Christianson
 * Date Created: June 25, 2026
 * Last Updated: June 28, 2026
 */

package com.snowboardstorefront;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starts the Snowboard Storefront Spring Boot application
 */
@SpringBootApplication
public class SnowboardStorefrontApplication {

	/**
	 * Main method that launches the web application
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(SnowboardStorefrontApplication.class, args);
	}

}