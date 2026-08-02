/*
 * File: OrderDAO.java
 * Description: Data access class for order records
 * Author: Zach Christianson
 * Date Created: July 26, 2026
 * Last Updated: July 26, 2026
 */

package com.snowboardstorefront.dao;

import com.snowboardstorefront.model.CartItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Handles database operations for the orders and order_items tables
 */
@Repository
public class OrderDAO {

    private final JdbcTemplate jdbcTemplate;
    private final CartDAO cartDAO;

    public OrderDAO(JdbcTemplate jdbcTemplate, CartDAO cartDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.cartDAO = cartDAO;
    }

    /**
     * Creates an order from the logged-in user's current cart
     *
     * @param userId logged-in user's ID
     * @return newly created order ID
     */
    public int placeOrder(int userId) {

        // Get the user's cart items and cart total
        List<CartItem> cartItems = cartDAO.findCartItemsByUserId(userId);
        BigDecimal cartTotal = cartDAO.calculateCartTotal(userId);

        if (cartItems.isEmpty()) {
            return 0;
        }

        // Create a new order record
        String orderSql = """
                INSERT INTO orders (user_id, status, total_amount)
                VALUES (?, ?, ?)
                """;

        jdbcTemplate.update(orderSql, userId, "pending", cartTotal);

        Integer orderId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

        if (orderId == null) {
            return 0;
        }

        // Add each cart item to the order_items table
        String orderItemSql = """
                INSERT INTO order_items (order_id, product_id, quantity, price_at_order)
                VALUES (?, ?, ?, ?)
                """;

        for (CartItem item : cartItems) {
            jdbcTemplate.update(
                    orderItemSql,
                    orderId,
                    item.getProductId(),
                    item.getQuantity(),
                    item.getPrice()
            );
        }

        // Clear the user's cart after the order is placed
        clearCart(userId);

        return orderId;
    }

    /**
     * Removes all items from the logged-in user's cart
     *
     * @param userId logged-in user's ID
     */
    public void clearCart(int userId) {
        String sql = """
                DELETE ci
                FROM cart_items ci
                JOIN cart c ON ci.cart_id = c.cart_id
                WHERE c.user_id = ?
                """;

        jdbcTemplate.update(sql, userId);
    }
}
