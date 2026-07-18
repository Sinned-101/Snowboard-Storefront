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

    public Profile findByUserId(int userId) {

        // SQL query to get all profile columns for the given user ID
        String sql = """
                SELECT profile_id, user_id, first_name, last_name, phone,
                       address_line, city, state, postal_code, country, bio
                FROM profile
                WHERE user_id = ?
                """;

        try {
            // queryForObject runs the query and expects exactly one row back
            // The lambda (row mapper) reads each column and builds a Profile object
            return jdbcTemplate.queryForObject(
                    sql,
                    (resultSet, rowNum) -> {
                        Profile profile = new Profile();
                        profile.setProfileId(resultSet.getInt("profile_id"));
                        profile.setUserId(resultSet.getInt("user_id"));
                        profile.setFirstName(resultSet.getString("first_name"));
                        profile.setLastName(resultSet.getString("last_name"));
                        profile.setPhone(resultSet.getString("phone"));
                        profile.setAddressLine(resultSet.getString("address_line"));
                        profile.setCity(resultSet.getString("city"));
                        profile.setState(resultSet.getString("state"));
                        profile.setPostalCode(resultSet.getString("postal_code"));
                        profile.setCountry(resultSet.getString("country"));
                        profile.setBio(resultSet.getString("bio"));
                        return profile;
                    },
                    userId
            );
        } catch (Exception exception) {
            // Return null if no profile is found for the given user ID
            return null;
        }
    }
}