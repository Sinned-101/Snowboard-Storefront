/*
 * File: Profile.java
 * Description: Model class for user profile information
 * Author: Zach Christianson
 * Date Created: June 27, 2026
 * Last Updated: June 27, 2026
 */

package com.snowboardstorefront.model;

/**
 * Represents profile information from the profile table
 */
public class Profile {

    // Unique ID for the profile record
    private int profileId;

    // User ID that connects the profile to a user account
    private int userId;

    // User's first name
    private String firstName;

    // User's last name
    private String lastName;

    // User's phone number
    private String phone;

    // User's street address
    private String addressLine;

    // User's city
    private String city;

    // User's state
    private String state;

    // User's postal code
    private String postalCode;

    // User's country
    private String country;

    // Profile biography or gear notes
    private String bio;

    /**
     * Default constructor
     */
    public Profile() {
    }

    /**
     * Gets the profile ID
     *
     * @return profile ID
     */
    public int getProfileId() {
        return profileId;
    }

    /**
     * Sets the profile ID
     *
     * @param profileId profile ID
     */
    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    /**
     * Gets the user ID connected to this profile
     *
     * @return user ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user ID connected to this profile
     *
     * @param userId user ID
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the user's first name
     *
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name
     *
     * @param firstName first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the user's last name
     *
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name
     *
     * @param lastName last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the user's phone number
     *
     * @return phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the user's phone number
     *
     * @param phone phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the user's street address
     *
     * @return street address
     */
    public String getAddressLine() {
        return addressLine;
    }

    /**
     * Sets the user's street address
     *
     * @param addressLine street address
     */
    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    /**
     * Gets the user's city
     *
     * @return city
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the user's city
     *
     * @param city city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the user's state
     *
     * @return state
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the user's state
     *
     * @param state state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the user's postal code
     *
     * @return postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the user's postal code
     *
     * @param postalCode postal code
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Gets the user's country
     *
     * @return country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the user's country
     *
     * @param country country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the user's biography or gear notes
     *
     * @return biography or gear notes
     */
    public String getBio() {
        return bio;
    }

    /**
     * Sets the user's biography or gear notes
     *
     * @param bio biography or gear notes
     */
    public void setBio(String bio) {
        this.bio = bio;
    }
}