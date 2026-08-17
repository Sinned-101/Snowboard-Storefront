/*
 * File: OrderDAO.java
 * Description: Data access class for order records
 * Author: Zach Christianson
 * Date Created: July 26, 2026
 * Last Updated: July 26, 2026
 */

package com.snowboardstorefront.dao;

import com.snowboardstorefront.model.CartItem;
import com.snowboardstorefront.model.DeliveryChannel;
import com.snowboardstorefront.model.InStoreChannel;
import com.snowboardstorefront.model.InStorePickupChannel;
import com.snowboardstorefront.model.Order;
import com.snowboardstorefront.model.OrderChannel;
import com.snowboardstorefront.model.OrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Creates an order from the cart, sets the channel, and clears the cart when done
    public int placeOrder(int userId, String channel, String shippingAddress) {

        // Get the user's cart items and cart total
        List<CartItem> cartItems = cartDAO.findCartItemsByUserId(userId);
        BigDecimal cartTotal = cartDAO.calculateCartTotal(userId);

        if (cartItems.isEmpty()) {
            return 0;
        }

        // In-store orders complete immediately - delivery and pickup orders start as pending
        String initialStatus = "in_store".equals(channel) ? "completed" : "pending";

        // picked_up is false (0) for new pickup orders, NULL for all other channels
        Integer pickedUpValue = "in_store_pickup".equals(channel) ? 0 : null;

        // Create a new order record with channel, optional shipping address, and picked_up flag
        String orderSql = """
                INSERT INTO orders (user_id, status, total_amount, channel, shipping_address, picked_up)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(orderSql, userId, initialStatus, cartTotal, channel, shippingAddress, pickedUpValue);

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

    // Builds an Order object from a database row, picking the right channel subclass
    private Order mapOrder(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        String channelType = rs.getString("channel");

        // Choose the correct channel subclass based on the channel column value
        OrderChannel channel;
        if ("in_store".equals(channelType)) {
            channel = new InStoreChannel();
        } else if ("in_store_pickup".equals(channelType)) {
            InStorePickupChannel pickup = new InStorePickupChannel();
            // getObject returns null if the column is NULL, Boolean.TRUE if value is 1
            // MySQL JDBC returns TINYINT(1) as Boolean, so we check for Boolean.TRUE directly
            Object pickedUpValue = rs.getObject("picked_up");
            pickup.setPickedUp(Boolean.TRUE.equals(pickedUpValue));
            channel = pickup;
        } else {
            DeliveryChannel delivery = new DeliveryChannel();
            delivery.setDeliveryAddress(rs.getString("shipping_address"));
            delivery.setTrackingNumber(rs.getString("tracking_number"));
            delivery.setShippedDate(rs.getTimestamp("shipped_date"));
            channel = delivery;
        }

        Order order = new Order(
                rs.getInt("order_id"),
                rs.getInt("user_id"),
                rs.getString("status"),
                rs.getBigDecimal("total_amount"),
                rs.getTimestamp("order_date"),
                channel
        );

        // Populate customer username if the query included a JOIN with users
        try {
            String username = rs.getString("customer_username");
            if (username != null) {
                order.setCustomerUsername(username);
            }
        } catch (Exception ignored) {
            // Column not present in this query - that's fine
        }

        return order;
    }

    // Returns all pending delivery and pickup orders for this customer (active orders)
    public List<Order> findActiveOrdersByUserId(int userId) {

        // Active = pending delivery orders OR pending in-store pickup orders not yet collected
        String sql = """
                SELECT order_id, user_id, status, total_amount, order_date,
                       channel, shipping_address, tracking_number, shipped_date, picked_up
                FROM orders
                WHERE user_id = ?
                  AND channel IN ('delivery', 'in_store_pickup')
                  AND status = 'pending'
                ORDER BY order_date DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapOrder(rs, rowNum), userId);
    }

    // Returns shipped, cancelled, completed, and in-store orders for this customer
    public List<Order> findCompletedOrdersByUserId(int userId) {

        // Completed = in-store orders, or delivery/pickup orders that are done
        String sql = """
                SELECT order_id, user_id, status, total_amount, order_date,
                       channel, shipping_address, tracking_number, shipped_date, picked_up
                FROM orders
                WHERE user_id = ? AND (channel = 'in_store' OR status IN ('shipped', 'delivered', 'cancelled', 'completed'))
                ORDER BY order_date DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapOrder(rs, rowNum), userId);
    }

    // Looks up a single order by its ID - returns null if not found
    public Order findOrderById(int orderId) {

        String sql = """
                SELECT order_id, user_id, status, total_amount, order_date,
                       channel, shipping_address, tracking_number, shipped_date, picked_up
                FROM orders
                WHERE order_id = ?
                """;

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapOrder(rs, rowNum), orderId);
        } catch (Exception e) {
            return null;
        }
    }

    // Returns all product line items for a given order, joined with product names
    public List<OrderItem> findOrderItemsByOrderId(int orderId) {

        // JOIN with product to get the product name alongside the order item data
        String sql = """
                SELECT oi.order_item_id, oi.order_id, oi.product_id,
                       p.name AS product_name, oi.quantity, oi.price_at_order
                FROM order_items oi
                JOIN product p ON oi.product_id = p.product_id
                WHERE oi.order_id = ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new OrderItem(
                rs.getInt("order_item_id"),
                rs.getInt("order_id"),
                rs.getInt("product_id"),
                rs.getString("product_name"),
                rs.getInt("quantity"),
                rs.getBigDecimal("price_at_order")
        ), orderId);
    }

    // Updates the shipping address on a pending delivery order
    public void updateOrderAddress(int orderId, String newAddress) {

        String sql = "UPDATE orders SET shipping_address = ? WHERE order_id = ? AND status = 'pending'";

        jdbcTemplate.update(sql, newAddress, orderId);
    }

    // Updates quantity for an item in a pending order, or removes the item if quantity is zero
    public void updateOrderItemQuantity(int orderItemId, int quantity) {

        if (quantity <= 0) {
            // Remove the item entirely if quantity is zero or less
            jdbcTemplate.update("DELETE FROM order_items WHERE order_item_id = ?", orderItemId);
        } else {
            jdbcTemplate.update(
                    "UPDATE order_items SET quantity = ? WHERE order_item_id = ?",
                    quantity, orderItemId
            );
        }
    }

    // Marks a pending delivery order as shipped and saves the tracking number and ship date
    public void markOrderShipped(int orderId, String trackingNumber) {

        String sql = """
                UPDATE orders
                SET status = 'shipped', tracking_number = ?, shipped_date = NOW()
                WHERE order_id = ? AND channel = 'delivery' AND status = 'pending'
                """;

        jdbcTemplate.update(sql, trackingNumber, orderId);
    }

    // Returns all delivery and pickup orders across every customer - used by the admin page
    public List<Order> findAllDeliveryOrders() {

        // JOIN with users to get the customer username alongside each order
        String sql = """
                SELECT o.order_id, o.user_id, o.status, o.total_amount, o.order_date,
                       o.channel, o.shipping_address, o.tracking_number, o.shipped_date, o.picked_up,
                       u.username AS customer_username
                FROM orders o
                JOIN users u ON o.user_id = u.user_id
                WHERE o.channel IN ('delivery', 'in_store_pickup')
                ORDER BY o.order_date DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapOrder(rs, rowNum));
    }

    // Marks an in-store pickup order as collected - sets status to completed and picked_up to 1
    public void markOrderPickedUp(int orderId) {

        // Sets status to completed and flags picked_up for the matching pickup order
        String sql = """
                UPDATE orders
                SET status = 'completed', picked_up = 1
                WHERE order_id = ? AND channel = 'in_store_pickup' AND status = 'pending'
                """;

        jdbcTemplate.update(sql, orderId);
    }

    // Updates the tracking number on a shipped order without touching the status or shipped date
    public void updateTrackingNumber(int orderId, String trackingNumber) {

        // Only updates the tracking number - status and shipped date are unchanged
        String sql = """
                UPDATE orders
                SET tracking_number = ?
                WHERE order_id = ? AND channel = 'delivery' AND status = 'shipped'
                """;

        jdbcTemplate.update(sql, trackingNumber, orderId);
    }

    // Cancels a pending delivery or pickup order - shipped and in-store orders cannot be cancelled
    public void cancelOrder(int orderId) {

        // Cancelled status makes the order inactive and permanently non-editable
        // In-store orders complete immediately and are excluded - only pending orders qualify
        String sql = """
                UPDATE orders
                SET status = 'cancelled'
                WHERE order_id = ?
                  AND channel IN ('delivery', 'in_store_pickup')
                  AND status = 'pending'
                """;

        jdbcTemplate.update(sql, orderId);
    }

    // Reverses a collected pickup order back to pending so the customer can still edit it
    public void reverseOrderPickup(int orderId) {

        // Clears the picked_up flag and returns the order to pending status
        String sql = """
                UPDATE orders
                SET status = 'pending', picked_up = 0
                WHERE order_id = ? AND channel = 'in_store_pickup' AND status = 'completed' AND picked_up = 1
                """;

        jdbcTemplate.update(sql, orderId);
    }

    // Reverses a shipped delivery order back to pending - tracking number is kept but ship date is cleared
    public void reverseOrderShipment(int orderId) {

        // Clears only the shipped date and returns the order to pending - tracking number is preserved
        String sql = """
                UPDATE orders
                SET status = 'pending', shipped_date = NULL
                WHERE order_id = ? AND channel = 'delivery' AND status = 'shipped'
                """;

        jdbcTemplate.update(sql, orderId);
    }

    // Removes all items from the cart after an order is placed
    public void clearCart(int userId) {
        String sql = """
                DELETE ci
                FROM cart_items ci
                JOIN cart c ON ci.cart_id = c.cart_id
                WHERE c.user_id = ?
                """;

        jdbcTemplate.update(sql, userId);
    }

    // Creates an order directly for a customer without going through their cart - used by admin
    public int createOrderForCustomer(int customerId, String channel, String shippingAddress, Map<Integer, Integer> productQuantities) {

        if (productQuantities.isEmpty()) {
            return 0;
        }

        // Look up the current price for each selected product and calculate the order total
        Map<Integer, BigDecimal> prices = new HashMap<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();
            BigDecimal price = jdbcTemplate.queryForObject(
                    "SELECT price FROM product WHERE product_id = ?",
                    BigDecimal.class,
                    productId
            );
            if (price != null) {
                prices.put(productId, price);
                totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(quantity)));
            }
        }

        // In-store orders complete immediately - delivery and pickup orders start as pending
        String initialStatus = "in_store".equals(channel) ? "completed" : "pending";
        Integer pickedUpValue = "in_store_pickup".equals(channel) ? 0 : null;

        String orderSql = """
                INSERT INTO orders (user_id, status, total_amount, channel, shipping_address, picked_up)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(orderSql, customerId, initialStatus, totalAmount, channel, shippingAddress, pickedUpValue);

        Integer orderId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        if (orderId == null) {
            return 0;
        }

        // Insert one order_items row per selected product using the price captured above
        String itemSql = """
                INSERT INTO order_items (order_id, product_id, quantity, price_at_order)
                VALUES (?, ?, ?, ?)
                """;

        for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();
            BigDecimal price = prices.get(productId);
            if (price != null) {
                jdbcTemplate.update(itemSql, orderId, productId, quantity, price);
            }
        }

        return orderId;
    }
}
