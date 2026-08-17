/*
 * File: OrderController.java
 * Description: Controller for customer order history pages
 * Author: Dennis Feldbruegge
 * Date Created: August 16, 2026
 * Last Updated: August 16, 2026
 */

package com.snowboardstorefront.controller;

import com.snowboardstorefront.dao.OrderDAO;
import com.snowboardstorefront.model.Order;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Handles customer order history - active orders, completed orders, and editing pending orders
@Controller
public class OrderController {

    private final OrderDAO orderDAO;

    // Spring injects the OrderDAO through the constructor
    public OrderController(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    // Shows the customer's active (pending delivery) orders
    @GetMapping("/orders")
    public String activeOrdersPage(HttpSession session, Model model) {

        // Redirect to login if no one is logged in
        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        // Fetch all pending delivery orders for this customer
        model.addAttribute("orders", orderDAO.findActiveOrdersByUserId(userId));

        return "orders";
    }

    // Shows the customer's completed orders - shipped, delivered, in-store, and cancelled
    @GetMapping("/orders/completed")
    public String completedOrdersPage(HttpSession session, Model model) {

        // Redirect to login if no one is logged in
        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        // Fetch all completed orders for this customer
        model.addAttribute("orders", orderDAO.findCompletedOrdersByUserId(userId));

        return "orders-completed";
    }

    // Shows the edit form for an active pending delivery order
    @GetMapping("/orders/{orderId}/edit")
    public String editOrderPage(
            @PathVariable int orderId,
            HttpSession session,
            Model model) {

        // Redirect to login if no one is logged in
        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        Order order = orderDAO.findOrderById(orderId);

        // Block access if the order doesn't exist, doesn't belong to this user, or isn't editable
        if (order == null || order.getUserId() != userId || !order.isEditable()) {
            return "redirect:/orders";
        }

        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderDAO.findOrderItemsByOrderId(orderId));

        return "order-edit";
    }

    // Saves changes to the shipping address on an active pending delivery order
    @PostMapping("/orders/{orderId}/edit/address")
    public String updateOrderAddress(
            @PathVariable int orderId,
            @RequestParam("shipping_address") String shippingAddress,
            HttpSession session) {

        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        Order order = orderDAO.findOrderById(orderId);

        // Only allow the owner of the order to edit it, and only if it is still editable
        if (order == null || order.getUserId() != userId || !order.isEditable()) {
            return "redirect:/orders";
        }

        orderDAO.updateOrderAddress(orderId, shippingAddress);

        return "redirect:/orders/" + orderId + "/edit";
    }

    // Updates the quantity of one item in an active pending delivery order
    @PostMapping("/orders/{orderId}/edit/quantity")
    public String updateOrderItemQuantity(
            @PathVariable int orderId,
            @RequestParam int orderItemId,
            @RequestParam int quantity,
            HttpSession session) {

        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        Order order = orderDAO.findOrderById(orderId);

        // Only allow the owner of the order to edit it, and only if it is still editable
        if (order == null || order.getUserId() != userId || !order.isEditable()) {
            return "redirect:/orders";
        }

        orderDAO.updateOrderItemQuantity(orderItemId, quantity);

        return "redirect:/orders/" + orderId + "/edit";
    }
}
