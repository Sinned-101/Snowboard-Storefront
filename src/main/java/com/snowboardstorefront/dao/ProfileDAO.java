/*
 * File: ProfileDAO.java
 * Description: Data access class for user profile records
 * Author: Zach Christianson
 * Date Created: June 28, 2026
 * Last Updated: June 28, 2026
 */

package com.snowboardstorefront.dao;

import com.snowboardstorefront.model.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Handles database operations for the profile table
 */
@Repository
public class ProfileDAO {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Initializes the ProfileDAO with the database helper object
     *
     * @param jdbcTemplate Spring database helper object
     */
    public ProfileDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Adds a new profile record to the profile table
     *
     * @param profile profile information entered during registration
     */
    public void createProfile(Profile profile) {
        String sql = """
                INSERT INTO profile (
                    user_id,
                    first_name,
                    last_name,
                    phone,
                    address_line,
                    city,
                    state,
                    postal_code,
                    country,
                    bio
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                profile.getUserId(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getPhone(),
                profile.getAddressLine(),
                profile.getCity(),
                profile.getState(),
                profile.getPostalCode(),
                profile.getCountry(),
                profile.getBio()
        );
    }
}