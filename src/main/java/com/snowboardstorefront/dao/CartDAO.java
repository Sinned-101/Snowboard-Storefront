/*
 * File: CartDAO.java
 * Description: Data access class for shopping cart records
 * Author: Zach Christianson
 * Date Created: July 23, 2026
 * Last Updated: July 23, 2026
 */

package com.snowboardstorefront.dao;

import com.snowboardstorefront.model.CartItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Handles database operations for the cart and cart_items tables
 */
@Repository
public class CartDAO {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Initializes the CartDAO with the database helper object
     *
     * @param jdbcTemplate Spring database helper object
     */
    public CartDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Finds the cart ID for a user
     *
     * @param userId logged-in user's ID
     * @return cart ID for the user, or 0 if no cart is found
     */
    public int findCartIdByUserId(int userId) {
        String sql = """
                SELECT cart_id
                FROM cart
                WHERE user_id = ?
                """;

        try {
            Integer cartId = jdbcTemplate.queryForObject(sql, Integer.class, userId);
            return cartId == null ? 0 : cartId;
        } catch (Exception exception) {
            return 0;
        }
    }

    /**
     * Creates a cart for a user
     *
     * @param userId logged-in user's ID
     * @return newly created cart ID
     */
    public int createCartForUser(int userId) {
        String sql = """
                INSERT INTO cart (user_id)
                VALUES (?)
                """;

        jdbcTemplate.update(sql, userId);

        Integer cartId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

        return cartId == null ? 0 : cartId;
    }

    /**
     * Finds an existing cart or creates one if the user does not have a cart
     *
     * @param userId logged-in user's ID
     * @return cart ID for the user
     */
    public int findOrCreateCartId(int userId) {
        int cartId = findCartIdByUserId(userId);

        if (cartId == 0) {
            cartId = createCartForUser(userId);
        }

        return cartId;
    }

    /**
     * Gets all cart items for a user's cart
     *
     * @param userId logged-in user's ID
     * @return list of cart items with product information
     */
    public List<CartItem> findCartItemsByUserId(int userId) {
        String sql = """
                SELECT 
                    ci.cart_item_id,
                    ci.cart_id,
                    ci.product_id,
                    p.name AS product_name,
                    p.price,
                    ci.quantity,
                    (p.price * ci.quantity) AS line_total
                FROM cart_items ci
                JOIN cart c ON ci.cart_id = c.cart_id
                JOIN product p ON ci.product_id = p.product_id
                WHERE c.user_id = ?
                ORDER BY p.name
                """;

        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new CartItem(
                resultSet.getInt("cart_item_id"),
                resultSet.getInt("cart_id"),
                resultSet.getInt("product_id"),
                resultSet.getString("product_name"),
                resultSet.getBigDecimal("price"),
                resultSet.getInt("quantity"),
                resultSet.getBigDecimal("line_total")
        ), userId);
    }

    /**
     * Calculates the total cost of all items in a user's cart
     *
     * @param userId logged-in user's ID
     * @return cart total amount
     */
    public BigDecimal calculateCartTotal(int userId) {
        String sql = """
                SELECT COALESCE(SUM(p.price * ci.quantity), 0)
                FROM cart_items ci
                JOIN cart c ON ci.cart_id = c.cart_id
                JOIN product p ON ci.product_id = p.product_id
                WHERE c.user_id = ?
                """;

        BigDecimal total = jdbcTemplate.queryForObject(sql, BigDecimal.class, userId);

        return total == null ? BigDecimal.ZERO : total;
    }

    /**
     * Adds a product to the user's cart
     * If the product is already in the cart, the quantity is increased by one
     *
     * @param userId logged-in user's ID
     * @param productId product ID to add to the cart
     */
    public void addProductToCart(int userId, int productId) {

        // Find or create a cart for the logged-in user
        int cartId = findOrCreateCartId(userId);

        String checkSql = """
            SELECT COUNT(*)
            FROM cart_items
            WHERE cart_id = ? AND product_id = ?
            """;

        Integer count = jdbcTemplate.queryForObject(
                checkSql,
                Integer.class,
                cartId,
                productId
        );

        if (count != null && count > 0) {

            // Increase the quantity if the product is already in the cart
            String updateSql = """
                UPDATE cart_items
                SET quantity = quantity + 1
                WHERE cart_id = ? AND product_id = ?
                """;

            jdbcTemplate.update(updateSql, cartId, productId);

        } else {

            // Add the product as a new cart item
            String insertSql = """
                INSERT INTO cart_items (cart_id, product_id, quantity)
                VALUES (?, ?, 1)
                """;

            jdbcTemplate.update(insertSql, cartId, productId);
        }
    }

    /**
     * Removes one item from the logged-in user's cart
     *
     * @param userId logged-in user's ID
     * @param cartItemId cart item ID to remove
     */
    public void removeCartItem(int userId, int cartItemId) {
        String sql = """
            DELETE ci
            FROM cart_items ci
            JOIN cart c ON ci.cart_id = c.cart_id
            WHERE ci.cart_item_id = ? AND c.user_id = ?
            """;

        jdbcTemplate.update(sql, cartItemId, userId);
    }

    /**
     * Updates the quantity for one item in the logged-in user's cart
     * If the quantity is zero or less, the item is removed from the cart
     *
     * @param userId logged-in user's ID
     * @param cartItemId cart item ID to update
     * @param quantity new item quantity
     */
    public void updateCartItemQuantity(int userId, int cartItemId, int quantity) {
        if (quantity <= 0) {
            removeCartItem(userId, cartItemId);
            return;
        }

        String sql = """
            UPDATE cart_items ci
            JOIN cart c ON ci.cart_id = c.cart_id
            SET ci.quantity = ?
            WHERE ci.cart_item_id = ? AND c.user_id = ?
            """;

        jdbcTemplate.update(sql, quantity, cartItemId, userId);
    }
}
