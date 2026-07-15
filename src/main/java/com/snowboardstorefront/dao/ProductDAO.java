/*
 * File: ProductDAO.java
 * Description: Data access class for product records
 * Author: Zach Christianson
 * Date Created: July 9, 2026
 * Last Updated: July 13, 2026
 */

package com.snowboardstorefront.dao;

import com.snowboardstorefront.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Handles database operations for the product table
 */
@Repository
public class ProductDAO {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Initializes the ProductDAO with the database helper object
     *
     * @param jdbcTemplate Spring database helper object
     */
    public ProductDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Gets all products with their category names
     *
     * @return list of all products
     */
    public List<Product> findAllProducts() {
        String sql = """
                SELECT 
                    p.product_id,
                    p.category_id,
                    p.name AS product_name,
                    p.description,
                    p.price,
                    p.stock_quantity,
                    c.name AS category_name
                FROM product p
                JOIN category c ON p.category_id = c.category_id
                ORDER BY p.name
                """;

        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new Product(
                resultSet.getInt("product_id"),
                resultSet.getInt("category_id"),
                resultSet.getString("product_name"),
                resultSet.getString("description"),
                resultSet.getBigDecimal("price"),
                resultSet.getInt("stock_quantity"),
                resultSet.getString("category_name")
        ));
    }

    /**
     * Gets all products that belong to a selected category
     *
     * @param categoryName category name selected by the user
     * @return list of products in the selected category
     */
    public List<Product> findProductsByCategory(String categoryName) {
        String sql = """
            SELECT 
                p.product_id,
                p.category_id,
                p.name AS product_name,
                p.description,
                p.price,
                p.stock_quantity,
                c.name AS category_name
            FROM product p
            JOIN category c ON p.category_id = c.category_id
            WHERE c.name = ?
            ORDER BY p.name
            """;

        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new Product(
                resultSet.getInt("product_id"),
                resultSet.getInt("category_id"),
                resultSet.getString("product_name"),
                resultSet.getString("description"),
                resultSet.getBigDecimal("price"),
                resultSet.getInt("stock_quantity"),
                resultSet.getString("category_name")
        ), categoryName);
    }

    /**
     * Finds one product by its product ID
     *
     * @param productId product ID from the database
     * @return matching product, or null if no product is found
     */
    public Product findProductById(int productId) {
        String sql = """
                SELECT 
                    p.product_id,
                    p.category_id,
                    p.name AS product_name,
                    p.description,
                    p.price,
                    p.stock_quantity,
                    c.name AS category_name
                FROM product p
                JOIN category c ON p.category_id = c.category_id
                WHERE p.product_id = ?
                """;

        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    (resultSet, rowNum) -> new Product(
                            resultSet.getInt("product_id"),
                            resultSet.getInt("category_id"),
                            resultSet.getString("product_name"),
                            resultSet.getString("description"),
                            resultSet.getBigDecimal("price"),
                            resultSet.getInt("stock_quantity"),
                            resultSet.getString("category_name")
                    ),
                    productId
            );
        } catch (Exception exception) {
            return null;
        }
    }
}
