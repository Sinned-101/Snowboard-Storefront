/*
 * File: User.java
 * Description: Model class for user account information
 * Author: Zach Christianson
 * Date Created: June 27, 2026
 * Last Updated: June 27, 2026
 */

package com.snowboardstorefront.model;

/**
 * Represents a user account from the users table
 */
public class User {

    // Unique ID for the user account
    private int userId;

    // Username used to identify the account
    private String username;

    // Email address for the account
    private String email;

    // Stored password hash from the database
    private String passwordHash;

    // User role
    private String role;

    /**
     * Default constructor
     */
    public User() {
    }

    /**
     * Creates a user object with account information
     *
     * @param userId       unique user ID
     * @param username     account username
     * @param email        account email address
     * @param passwordHash stored password hash
     * @param role         account role
     */
    public User(int userId, String username, String email, String passwordHash, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    /**
     * Gets the user ID
     *
     * @return user ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user ID
     *
     * @param userId user ID
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the username
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username
     *
     * @param username account username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the email address
     *
     * @return email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address
     *
     * @param email account email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the stored password hash
     *
     * @return password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the stored password hash
     *
     * @param passwordHash password hash
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Gets the user role
     *
     * @return user role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the user role
     *
     * @param role user role
     */
    public void setRole(String role) {
        this.role = role;
    }
}