/*
 * File: UserDAO.java
 * Description: Data access class for user account records
 * Author: Zach Christianson
 * Date Created: June 28, 2026
 * Last Updated: June 28, 2026
 */

package com.snowboardstorefront.dao;

import com.snowboardstorefront.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Handles database operations for the users table
 */
@Repository
public class UserDAO {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Initializes the UserDAO with the database helper object
     *
     * @param jdbcTemplate Spring database helper object
     */
    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Checks whether a username or email is already used
     *
     * @param username username entered during registration
     * @param email email entered during registration
     * @return true if the username or email already exists
     */
    public boolean userExists(String username, String email) {
        String sql = """
                SELECT COUNT(*)
                FROM users
                WHERE username = ? OR email = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username, email);

        return count != null && count > 0;
    }

    /**
     * Adds a new user account to the users table
     *
     * @param user user account information
     * @return the new user's generated user ID
     */
    public int createUser(User user) {
        String sql = """
                INSERT INTO users (username, email, password_hash, role)
                VALUES (?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole()
        );

        String idSql = "SELECT LAST_INSERT_ID()";

        Integer userId = jdbcTemplate.queryForObject(idSql, Integer.class);

        return userId == null ? 0 : userId;
    }

    /**
     * Finds a user by username or email to log them in
     *
     * @param loginIdentifier username or email entered during login
     * @return matching user account, or null if no user is found
     */
    public User findByUsernameOrEmail(String loginIdentifier) {
        String sql = """
            SELECT user_id, username, email, password_hash, role
            FROM users
            WHERE username = ? OR email = ?
            """;

        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    (resultSet, rowNum) -> new User(
                            resultSet.getInt("user_id"),
                            resultSet.getString("username"),
                            resultSet.getString("email"),
                            resultSet.getString("password_hash"),
                            resultSet.getString("role")
                    ),
                    loginIdentifier,
                    loginIdentifier
            );
        } catch (Exception exception) {
            return null;
        }
    }

    public User findById(int userId) {

        // SQL query to get all user columns for the given user ID
        String sql = """
                SELECT user_id, username, email, password_hash, role
                FROM users
                WHERE user_id = ?
                """;

        try {
            // queryForObject runs the query and expects exactly one row back
            // The lambda reads each column and builds a User object
            return jdbcTemplate.queryForObject(
                    sql,
                    (resultSet, rowNum) -> new User(
                            resultSet.getInt("user_id"),
                            resultSet.getString("username"),
                            resultSet.getString("email"),
                            resultSet.getString("password_hash"),
                            resultSet.getString("role")
                    ),
                    userId
            );
        } catch (Exception exception) {
            // Return null if no user is found for the given ID
            return null;
        }
    }

    // Get every user account from the database for the admin user list page
    public List<User> findAllUsers() {

        // SQL selects all users ordered alphabetically by username
        String sql = """
                SELECT user_id, username, email, password_hash, role
                FROM users
                ORDER BY username
                """;

        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new User(
                resultSet.getInt("user_id"),
                resultSet.getString("username"),
                resultSet.getString("email"),
                resultSet.getString("password_hash"),
                resultSet.getString("role")
        ));
    }

    // Replace the stored password hash with a newly generated BCrypt hash
    public void updatePassword(int userId, String newPasswordHash) {

        // SQL updates only the password_hash column for the matching user
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";

        jdbcTemplate.update(sql, newPasswordHash, userId);
    }

    // Change the role of a user account - used by admin to promote or demote users
    public void updateRole(int userId, String role) {

        // SQL updates only the role column for the matching user
        String sql = "UPDATE users SET role = ? WHERE user_id = ?";

        jdbcTemplate.update(sql, role, userId);
    }
}