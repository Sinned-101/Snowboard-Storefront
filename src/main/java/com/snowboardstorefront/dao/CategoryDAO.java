/*
 * File: CategoryDAO.java
 * Description: Data access class for product category records
 * Author: Zach Christianson
 * Date Created: July 9, 2026
 * Last Updated: July 9, 2026
 */

package com.snowboardstorefront.dao;

import com.snowboardstorefront.model.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Handles database operations for the category table
 */
@Repository
public class CategoryDAO {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Initializes the CategoryDAO with the database helper object
     *
     * @param jdbcTemplate Spring database helper object
     */
    public CategoryDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Gets all product categories
     *
     * @return list of all categories
     */
    public List<Category> findAllCategories() {
        String sql = """
                SELECT category_id, name AS category_name
                FROM category
                ORDER BY name
                """;

        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new Category(
                resultSet.getInt("category_id"),
                resultSet.getString("category_name")
        ));
    }
}
